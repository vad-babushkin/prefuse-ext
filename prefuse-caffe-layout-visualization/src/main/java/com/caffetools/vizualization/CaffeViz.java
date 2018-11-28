package com.caffetools.vizualization;

import com.caffetools.javacc.CaffeParser;
import com.caffetools.javacc.ParseException;
import com.caffetools.vizualization.prefuse.GraphView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.util.io.SimpleFileFilter;
import prefuse.util.ui.UILib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CaffeViz {

    public static final Log log = LogFactory.getLog(CaffeViz.class);

    public static final String OBJECT = "object";
    public static final String TOOLTIP = "tooltip";
    public static final String LABEL = "label";
    /** Node table schema used for generated Graphs */
    public static final Schema LABEL_SCHEMA = new Schema();
    static {
        LABEL_SCHEMA.addColumn(TOOLTIP, String.class, "");
        LABEL_SCHEMA.addColumn(LABEL, String.class, "");
        LABEL_SCHEMA.addColumn("type", String.class, "");
        //LABEL_SCHEMA.addColumn("typeColor", int.class, ColorLib.rgb(255, 29, 44)); new Color(255, 29, 44);
    }

/*
    public static final Map<String, Integer> typeColorMap = new HashMap(){{
        put("HDF5_DATA", ColorLib.rgb(178, 167, 255)); new Color(178, 167, 255);
        put("CONVOLUTION", ColorLib.rgb(61, 255, 43)); new Color(61, 255, 43);
    }};

*/
    public static class PrefuseGraph {

        private Map<String, Pair<Node, CaffeBlock> > index = new HashMap<>() ;
        private Graph graph;

        public PrefuseGraph(Map<String, CaffeBlock> map) {
            createGraph(map);
        }

        public void createGraph(Map<String, CaffeBlock> map) {
            graph = new Graph(true);
            graph.getNodeTable().addColumns(LABEL_SCHEMA);
            
            map.values().stream().forEach(b -> {
                final Pair<Node, CaffeBlock> pcurrent = getOrCreate(map, b);
                final Node currentNode = pcurrent.getFirst();

                b.getPropertyStrings(CaffeBlock.TOP).stream()
                    .forEach(topName -> {
                        final Pair<Node, CaffeBlock> top = getOrCreate(map, topName);
                        if(top!=null) {
                            final Node topNode = top.getFirst();
                            if (graph.getEdge(currentNode, topNode) == null) {
                                graph.addEdge(currentNode, topNode);
                            }
                        }
                    });

                b.getPropertyStrings(CaffeBlock.BOTTOM).stream()
                    .forEach(bottomName->{
                        final Pair<Node, CaffeBlock> bottom = getOrCreate(map, bottomName);
                        if (bottom!=null) {
                            final Node bottomNode = bottom.getFirst();
                            if (graph.getEdge(bottomNode, currentNode)==null) {
                                graph.addEdge(bottomNode, currentNode);
                            }
                        }
                    });
            });
        }
        
        public Pair<Node, CaffeBlock> getOrCreate(Map<String, CaffeBlock> map, CaffeBlock current){
            final String name = current.getName();

            Pair<Node, CaffeBlock> pair = index.get(name);
            if(pair==null){

                pair = createPair(name, current);
            }
            return pair;
        }

        public Pair<Node, CaffeBlock> getOrCreate(Map<String, CaffeBlock> map, String name){

            if(name.equals("label"))
                return null;

            Pair<Node, CaffeBlock> pair = index.get(name);
            if(pair==null){
                CaffeBlock current = map.get(name);
                if(current==null) {
                    log.error("Name:" + name + " not found");
                    current = new CaffeBlock("layers");
                    current.add(CaffeBlock.NAME, name);
                    current.add(CaffeBlock.TYPE, "UNKNOWN");
                }
                pair = createPair(name, current);
            }
            return pair;
        }

        private Pair<Node, CaffeBlock> createPair(String name, CaffeBlock current) {
            Pair<Node, CaffeBlock> pair;
            final Node node = graph.addNode();

            final String type = current.getType();
            node.setString(LABEL, name + " : " + type);
            node.setString(TOOLTIP, "<html>" + current.toHtml() + "</html>");
            node.setString("type", type);

            //final Integer color = typeColorMap.get(type);
            //node.setInt("typeColor", color != null ? color : ColorLib.rgb(100,100,130)); new Color(100,100,130);
            //node.set(OBJECT, current);

            pair = new Pair<Node, CaffeBlock>(node, current);
            index.put(name, pair);
            return pair;
        }


        public Graph getGraph() {
            return graph;
        }
    }

    public static Map<String, CaffeBlock> toMap(List<CaffeBlock> blocks){
        Map<String, CaffeBlock> m = new HashMap<>();
        for (CaffeBlock block : blocks) {
            String name = block.getName();
            if(name==null)
                log.error("name==null for "+block);
            m.put(name, block);
        }
        return m;
    }

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        //FileReader r = new FileReader("D:\\apps\\cuda\\caffe-windows\\examples\\kaggle-bowl\\caffe_windows\\model\\vgg_googlenet_maxout_48x48_train_valid.prototxt");
        //final File file = new File("D:\\apps\\cuda\\caffe-windows\\examples\\kaggle-bowl\\caffe_windows\\model\\vgg_googlenet_maxout_48x48_train_valid.prototxt");
        //final File file = new File("D:\\apps\\cuda\\caffe-windows\\examples\\kaggle-bowl\\caffe_windows\\model\\googlenet_train_val.prototxt");
        //final File file = new File("D:\\projects\\dcrd-branches\\nn-experiments\\BHF\\new.corpus.preparation.x0.2\\lenet_BN_train_valid.prototxt");
        final File file = new File("src/main/resources/lenet_BN_train_valid.prototxt");  //default
        PrefuseGraph pg = getPrefuseGraph(file);
        if(pg == null) {     //Empty graph
            Graph graph = new Graph(true);
            graph.getNodeTable().addColumns(LABEL_SCHEMA);
            runGraphView(graph);
        }
        else
            runGraphView(pg.getGraph());
        return;


    }

    public static File lastDir = new File(".");
    public static PrefuseGraph getPrefuseGraph(File file) throws FileNotFoundException, ParseException {
        if(!file.exists())
            return null;

        FileReader r = new FileReader(file);

        CaffeParser p = new CaffeParser(r);

        p.caffeFile();

        System.out.println(p.getPrototxtNodes());

        lastDir = file.getParentFile();
        return new PrefuseGraph(toMap(p.getPrototxtNodes()));
    }

    public static Graph getGraph(Component parent) throws FileNotFoundException, ParseException {
        final JFileChooser jfc = new JFileChooser();
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setDialogTitle("Open .prototxt");
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setCurrentDirectory(lastDir);

        SimpleFileFilter ff;

        // TreeML
        ff = new SimpleFileFilter("prototxt",
            "Caffe Model File (*.prototxt)");
        ff.addExtension("prototxt");
        jfc.setFileFilter(ff);

        int retval = jfc.showOpenDialog(parent);
        if (retval != JFileChooser.APPROVE_OPTION)
            return null;

        File f = jfc.getSelectedFile();

        return getPrefuseGraph(f).getGraph();
    }

    private static void runGraphView(Graph g) {
        UILib.setPlatformLookAndFeel();

        final GraphView view = new GraphView(g, LABEL, TOOLTIP);

        // set up menu
        JMenu dataMenu = new JMenu("File");
        dataMenu.add(new OpenGraphAction(view));

        JMenuBar menubar = new JMenuBar();
        menubar.add(dataMenu);

        // launch window
        JFrame frame = new JFrame("p r e f u s e  |  g r a p h v i e w");
        frame.setJMenuBar(menubar);
        frame.setContentPane(view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                view.getVisualization().run("layout");
            }

            public void windowDeactivated(WindowEvent e) {
                view.getVisualization().cancel("layout");
            }
        });

    }

    public static class OpenGraphAction extends AbstractAction {
        private GraphView m_view;

        public OpenGraphAction(GraphView view) {
            m_view = view;
            this.putValue(AbstractAction.NAME, "Open File...");
            this.putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("ctrl O"));
        }

        public void actionPerformed(ActionEvent e) {
            Graph g = null;
            try {
                g = getGraph(m_view);
            } catch (FileNotFoundException e1) {
                JOptionPane.showMessageDialog(m_view, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException e1) {
                JOptionPane.showMessageDialog(m_view, "Parse error:"+e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (g == null) return;

            m_view.setGraph(g, LABEL);
        }
    }

}
