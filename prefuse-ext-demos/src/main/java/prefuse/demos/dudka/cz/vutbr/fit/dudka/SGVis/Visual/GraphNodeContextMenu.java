package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.util.FontLib;
import prefuse.util.collections.IntIterator;
import prefuse.visual.VisualItem;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Config;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.DataLib;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.RelationStorage;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

/**
 * Per-node context menu used in GraphView class.
 */
class GraphNodeContextMenu {
	private class ItemBrowseActionListener implements ActionListener {
		private URL url;
		public ItemBrowseActionListener(URL url) {
			this.url = url;
		}
		public void actionPerformed(ActionEvent e) {
			view.showStatus("Opening browser at "+url.toString());
			try {
				BrowserLauncher launcher = new BrowserLauncher();
				launcher.openURLinBrowser(this.url.toString());
			} catch (BrowserLaunchingInitializingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedOperatingSystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private class ItemDeleteActionListener implements ActionListener {
		private final GraphView view;
		private final int node;
		public ItemDeleteActionListener(GraphView view, int node) {
			this.view = view;
			this.node = node;
		}
		public void actionPerformed(ActionEvent e) {
			view.removeNode(node);
		}
	}
	
	private class ItemLookupActionListener implements ActionListener {
		private GraphView view;
		private URL url;
		public ItemLookupActionListener(GraphView view, URL url) {
			this.view = view;
			this.url = url;
		}
		public void actionPerformed(ActionEvent e) {
			view.lookup(url);
		}
	}
	
	private class ItemCollapseActionListener implements ActionListener {
		private GraphView view;
		private String host;
		public ItemCollapseActionListener(GraphView view, String host) {
			this.view = view;
			this.host = host;
		}
		public void actionPerformed(ActionEvent e) {
			view.collapseHost(host);
		}
	}
	
	private class ItemExpandActionListener implements ActionListener {
		private GraphView view;
		private String host;
		public ItemExpandActionListener(GraphView view, String host) {
			this.view = view;
			this.host = host;
		}
		public void actionPerformed(ActionEvent e) {
			view.expandHost(host);
		}
	}
	GraphView view;
	public GraphNodeContextMenu(GraphView view) {
		this.view = view;
	}
	private JPopupMenu getContextMenu(VisualItem item) {
		JPopupMenu menu = new JPopupMenu();
		JLabel name = new JLabel(" "+item.getString("text"));
		name.setFont(FontLib.getFont("Tahoma", 18));
		menu.add(name);
		menu.addSeparator();

		try {
			String text = item.getString("text");
			String type = item.getString("type");
			if (type == "G") {
				// Expand menu item
				JMenuItem itemExpand = new JMenuItem("Expand");
				itemExpand.addActionListener(
						new ItemExpandActionListener(view, text)
				);
				menu.add(itemExpand);

			} else /*if (type == "U")*/ {
				// Collapse menu item
				JMenuItem itemCollapse = new JMenuItem("Collapse");
				itemCollapse.addActionListener(
						new ItemCollapseActionListener(
								view,
								RelationStorage.urlToHost(new URL(text)))
				);
				menu.add(itemCollapse);
			}

			// delete item
			final int node = item.getRow();
			IntIterator iter = view.edgeRows(node);
			if (!DataLib.hasMinSize(iter, 2)) {
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(
						new ItemDeleteActionListener(view, node)
				);
				menu.add(delete);
			}

			// lookup item
			ListenerFactory lookupFactory = new ListenerFactory() {
				public ActionListener createListener(URL url) {
					return new ItemLookupActionListener(view, url);
				}
			};
			JMenuItem lookupMenu = createPerUrlEntry(item, "Lookup", lookupFactory);
			if (LookupWorker.getActiveCount()>=Config.MAX_CONCURRENT_LOOKUPS)
				lookupMenu.setEnabled(false);
			menu.add(lookupMenu);

			// browse item
			menu.addSeparator();
			ListenerFactory browseFactory = new ListenerFactory() {
				public ActionListener createListener(URL url) {
					return new ItemBrowseActionListener(url);
				}
			};
			menu.add(createPerUrlEntry(item, "Open in browser", browseFactory));
		}
		catch (MalformedURLException e) {
			throw new AssertionError("malformed URL");
		}

		return menu;
	}
	private interface ListenerFactory {
		ActionListener createListener(URL url);
	}
	private JMenuItem createPerUrlEntry(
			VisualItem item,
			String itemText,
			ListenerFactory factory)
	{
		JMenuItem menuItem = null;

		String text = item.getString("text");
		String type = item.getString("type");
		if (type == "G") {
			RelationStorage storage = view.getStorage();
			Iterable<URL> urls = storage.getUrls(text);
			if (DataLib.hasMinSize(urls, 2)) {
				menuItem = new JMenu(itemText);
				for (URL u: urls) 
					menuItem.add(createItem(u.toString(), u, factory));
			} else if (urls.iterator().hasNext()) {
				URL url = urls.iterator().next();
				menuItem = createItem(itemText + " - " + url.toString(), url, factory);
			} else {
				throw new AssertionError("Group with no items");
			}
		} else /*if (type == "U")*/ {
			try {
				menuItem = createItem(itemText,	new URL(text), factory);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return menuItem;
	}
	private JMenuItem createItem(
			String text,
			URL url,
			ListenerFactory factory)
	{
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(factory.createListener(url));
		return item;
	}
	public void showContextMenu(VisualItem item, MouseEvent e) {
		JPopupMenu menu = this.getContextMenu(item);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	public Control getControlListener() {
		final GraphNodeContextMenu obj = this;
		return new ControlAdapter() {
        	public void itemClicked(VisualItem item, MouseEvent e) {
        		if (e.getButton()!=3)
        			// not right click
        			return;
        		
        		obj.showContextMenu(item, e);
        	}
		};
	}

}
