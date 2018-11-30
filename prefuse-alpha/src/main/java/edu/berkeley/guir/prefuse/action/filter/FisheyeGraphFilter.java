//package edu.berkeley.guir.prefuse.action.filter;
//
//import edu.berkeley.guir.prefuse.EdgeItem;
//import edu.berkeley.guir.prefuse.ItemRegistry;
//import edu.berkeley.guir.prefuse.NodeItem;
//import edu.berkeley.guir.prefuse.focus.FocusSet;
//import edu.berkeley.guir.prefuse.graph.DefaultGraph;
//import edu.berkeley.guir.prefuse.graph.Edge;
//import edu.berkeley.guir.prefuse.graph.Graph;
//import edu.berkeley.guir.prefuse.graph.Node;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//
//public class FisheyeGraphFilter
//  extends Filter
//{
//  public static final String[] ITEM_CLASSES = { "node", "edge" };
//  public static final int DEFAULT_MIN_DOI = -2;
//  public static final String ATTR_CENTER = "center";
//  private int m_minDOI;
//  private boolean m_edgesVisible = true;
//  private List m_queue = new LinkedList();
//
//  public FisheyeGraphFilter()
//  {
//    this(-2);
//  }
//
//  public FisheyeGraphFilter(int paramInt)
//  {
//    this(paramInt, true);
//  }
//
//  public FisheyeGraphFilter(int paramInt, boolean paramBoolean)
//  {
//    this(paramInt, paramBoolean, true);
//  }
//
//  public FisheyeGraphFilter(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
//  {
//    super(ITEM_CLASSES, paramBoolean2);
//    this.m_minDOI = paramInt;
//    this.m_edgesVisible = paramBoolean1;
//  }
//
//  protected Iterator getFoci(ItemRegistry paramItemRegistry)
//  {
//    Iterator localIterator = paramItemRegistry.getDefaultFocusSet().iterator();
//    if (!localIterator.hasNext()) {
//      localIterator = Collections.EMPTY_LIST.iterator();
//    }
//    return localIterator;
//  }
//
//  public void run(ItemRegistry paramItemRegistry, double paramDouble)
//  {
//    Graph localGraph = paramItemRegistry.getGraph();
//    Object localObject1 = paramItemRegistry.getFilteredGraph();
//    if ((localObject1 instanceof DefaultGraph)) {
//      ((DefaultGraph)localObject1).reinit(localGraph.isDirected());
//    } else {
//      localObject1 = new DefaultGraph(localGraph.isDirected());
//    }
//    Iterator localIterator1 = getFoci(paramItemRegistry);
//    break label197;
//    label61:
//    Object localObject3;
//    Object localObject4;
//    int i;
//    do
//    {
//      do
//      {
//        if (!localIterator1.hasNext()) {
//          break;
//        }
//        localObject2 = localIterator1.next();
//      } while (!(localObject2 instanceof Node));
//      localObject3 = (Node)localObject2;
//      localObject4 = paramItemRegistry.getNodeItem((Node)localObject3);
//      i = 0;
//      i = (localObject4 == null) || (((NodeItem)localObject4).getDirty() > 0) || (((NodeItem)localObject4).getDOI() < 0.0D) ? 1 : 0;
//      if ((localObject4 == null) || (((NodeItem)localObject4).getDirty() > 0)) {
//        localObject4 = paramItemRegistry.getNodeItem((Node)localObject3, true);
//      }
//      ((Graph)localObject1).addNode((Node)localObject4);
//    } while (i == 0);
//    ((NodeItem)localObject4).setDOI(0.0D);
//    this.m_queue.add(localObject4);
//    label197:
//    Object localObject5;
//    Node localNode1;
//    for (;;)
//    {
//      if (this.m_queue.isEmpty()) {
//        break label61;
//      }
//      localObject5 = (NodeItem)this.m_queue.remove(0);
//      localNode1 = (Node)((NodeItem)localObject5).getEntity();
//      double d = ((NodeItem)localObject5).getDOI() - 1.0D;
//      if (d < this.m_minDOI) {
//        break;
//      }
//      Iterator localIterator3 = localNode1.getNeighbors();
//      int j = 0;
//      while (localIterator3.hasNext())
//      {
//        Node localNode2 = (Node)localIterator3.next();
//        NodeItem localNodeItem2 = paramItemRegistry.getNodeItem(localNode2);
//        i = (localNodeItem2 == null) || (localNodeItem2.getDirty() > 0) || (localNodeItem2.getDOI() < d) ? 1 : 0;
//        if ((localNodeItem2 == null) || (localNodeItem2.getDirty() > 0)) {
//          localNodeItem2 = paramItemRegistry.getNodeItem(localNode2, true);
//        }
//        ((Graph)localObject1).addNode(localNodeItem2);
//        if (i != 0)
//        {
//          localNodeItem2.setDOI(d);
//          this.m_queue.add(localNodeItem2);
//        }
//      }
//    }
//    Object localObject2 = paramItemRegistry.getNodeItems();
//    while (((Iterator)localObject2).hasNext())
//    {
//      localObject3 = (NodeItem)((Iterator)localObject2).next();
//      if (((NodeItem)localObject3).getDirty() <= 0)
//      {
//        localObject4 = (Node)((NodeItem)localObject3).getEntity();
//        Iterator localIterator2 = ((Node)localObject4).getEdges();
//        while (localIterator2.hasNext())
//        {
//          localObject5 = (Edge)localIterator2.next();
//          localNode1 = ((Edge)localObject5).getAdjacentNode((Node)localObject4);
//          NodeItem localNodeItem1 = paramItemRegistry.getNodeItem(localNode1);
//          if ((localNodeItem1 != null) && (localNodeItem1.getDirty() == 0))
//          {
//            EdgeItem localEdgeItem = paramItemRegistry.getEdgeItem((Edge)localObject5, true);
//            ((Graph)localObject1).addEdge(localEdgeItem);
//            if (!this.m_edgesVisible) {
//              localEdgeItem.setVisible(false);
//            }
//          }
//        }
//      }
//    }
//    paramItemRegistry.setFilteredGraph((Graph)localObject1);
//    super.run(paramItemRegistry, paramDouble);
//  }
//
//  public boolean isEdgesVisible()
//  {
//    return this.m_edgesVisible;
//  }
//
//  public void setEdgesVisible(boolean paramBoolean)
//  {
//    this.m_edgesVisible = paramBoolean;
//  }
//
//  public int getMinDOI()
//  {
//    return this.m_minDOI;
//  }
//
//  public void setMinDOI(int paramInt)
//  {
//    this.m_minDOI = paramInt;
//  }
//}
//
//
///* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/FisheyeGraphFilter.class
// * Java compiler version: 2 (46.0)
// * JD-Core Version:       0.7.1
// */