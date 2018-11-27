package prefuse.demos.qazxiaye;/*
 * Copyright @ Ye XIA <qazxiaye@126.com>
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import prefuse.Visualization;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualItem;

public class DeviceGenerator {
    static String mobile_link;
    static String house_link;
    static String wifi_link;
    static String other_link;

    static Graph graph;
    static Visualization vis;
    AggregateTable aggTable;
    static List<AggregateItem> aggItems;

    static List<Integer> lowPopIds;
    static List<Integer> bsIds;
    static Map<Integer, Integer> boxIds;
    static Map<Integer, MobileUpdater> mobileUpdaters;

    static final String GRAPH = "graph";

    static Random random;
    int popLevel;
    int highPopCount = 0;

    public DeviceGenerator(String input) {
        aggItems = new ArrayList<AggregateItem>();

        lowPopIds = new ArrayList<Integer>();
        bsIds = new ArrayList<Integer>();
        boxIds = new HashMap<Integer, Integer>();
        mobileUpdaters = new HashMap<Integer, MobileUpdater>();

        random = new Random();

        graph = new Graph();
        graph.addColumn("name", String.class);
        graph.addColumn("speed", String.class);
        graph.addColumn("core", Integer.class);
        graph.addColumn("clusterNb", Integer.class);
        graph.addColumn("level", Integer.class);
        graph.addColumn("link", String.class);
        graph.addColumn("state", Integer.class);
        graph.addColumn("img", String.class);

        vis = new Visualization();
        vis.add(GRAPH, graph);

        aggTable = vis.addAggregates("aggregates");
        aggTable.addColumn(VisualItem.POLYGON, float[].class);
        aggTable.addColumn("id", int.class);

        try {
            File file = new File(input);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            mobile_link = doc.getElementsByTagName("mobile_link").item(0).getFirstChild().getNodeValue();
            house_link = doc.getElementsByTagName("house_link").item(0).getFirstChild().getNodeValue();
            wifi_link = doc.getElementsByTagName("wifi_link").item(0).getFirstChild().getNodeValue();
            other_link = doc.getElementsByTagName("other_link").item(0).getFirstChild().getNodeValue();

            String s[] = doc.getElementsByTagName("pop_nb").item(0).getFirstChild().getNodeValue().split(" ");
            popLevel = s.length;

            for (int level = 0; level < popLevel; level++) {
                int nb = Integer.parseInt(s[level]);
                for (int i = 0; i < nb; i++) {
                    Node n = graph.addNode();
                    n.set("level", level);
                }
            }

            NodeList nodeList = doc.getElementsByTagName("pop");
            for (int i = 0; i < nodeList.getLength(); i++) {
                CreatePOP((Element) nodeList.item(i));
            }

            nodeList = doc.getElementsByTagName("cloud");
            for (int i = 0; i < nodeList.getLength(); i++) {
                CreateCloud((Element) nodeList.item(i));
            }

            nodeList = doc.getElementsByTagName("house");
            for (int i = 0; i < nodeList.getLength(); i++) {
                CreateHouseDevices((Element) nodeList.item(i));
            }

            nodeList = doc.getElementsByTagName("bs");
            for (int i = 0; i < nodeList.getLength(); i++) {
                CreateBS((Element) nodeList.item(i));
            }

            nodeList = doc.getElementsByTagName("mobile");
            for (int i = 0; i < nodeList.getLength(); i++) {
                CreateMobile((Element) nodeList.item(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CreateCloud(Element e) {
        int id = Integer.parseInt(e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
        String capacity[] = e.getElementsByTagName("capacity").item(0).getFirstChild().getNodeValue().split(" ");
        String clusterNb = e.getElementsByTagName("nb").item(0).getFirstChild().getNodeValue();
        String connects[] = e.getElementsByTagName("connect").item(0).getFirstChild().getNodeValue().split(" ");

        Node n = graph.addNode();
        n.set("name", "Cloud " + id);
        n.set("speed", capacity[0]);
        n.set("core", Integer.parseInt(capacity[1]));
        n.set("img", "Cloud");
        n.set("clusterNb", Integer.parseInt(clusterNb));
        n.set("level", -1);

        for (String s : connects) {
            int connect = GetPopIndex(s);
            if (connect >= 0) {
                Edge edge = graph.addEdge(n, graph.getNode(connect));
                edge.set("link", other_link);
                edge.set("state", 0);
            }
        }
    }

    private void CreatePOP(Element e) {
        int id = Integer.parseInt(e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
        String capacity[] = e.getElementsByTagName("capacity").item(0).getFirstChild().getNodeValue().split(" ");

        Node n = graph.getNode(id);
        n.set("name", "POP " + id);
        n.set("speed", capacity[0]);
        n.set("core", Integer.parseInt(capacity[1]));

        if (e.getElementsByTagName("connect").getLength() > 0) {
            String connects[] = e.getElementsByTagName("connect").item(0).getFirstChild().getNodeValue().split(" ");

            for (String s : connects) {
                int connect = GetPopIndex(s);
                if (connect >= 0) {
                    int i = graph.addEdge(id, connect);
                    graph.getEdge(i).set("link", other_link);
                    graph.getEdge(i).set("state", 0);
                }
            }
        }

        if (e.getElementsByTagName("cover").getLength() > 0) {
            n.set("img", "POP_low");
            lowPopIds.add(id);
        } else {
            n.set("img", "POP_high");
            highPopCount++;
        }
    }

    private void CreateBS(Element e) {
        int id = Integer.parseInt(e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
        String capacity[] = e.getElementsByTagName("capacity").item(0).getFirstChild().getNodeValue().split(" ");
        String connects[] = e.getElementsByTagName("connect").item(0).getFirstChild().getNodeValue().split(" ");

        int bsId = graph.addNodeRow();
        Node n = graph.getNode(bsId);
        n.set("name", "BS " + id);
        n.set("speed", capacity[0]);
        n.set("core", Integer.parseInt(capacity[1]));
        n.set("img", "BS");
        n.set("level", popLevel);

        bsIds.add(bsId);

        for (String s : connects) {
            int connect = GetPopIndex(s);
            if (connect >= 0) {
                Edge edge = graph.addEdge(n, graph.getNode(connect));
                edge.set("link", other_link);
                edge.set("state", 0);
            }
        }
    }

    private void CreateHouseDevices(Element e) {
        int id = Integer.parseInt(e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
        String boxCapacity[] = e.getElementsByTagName("box").item(0).getFirstChild().getNodeValue().split(" ");

        int boxId = graph.addNodeRow();
        Node box = graph.getNode(boxId);
        box.set("name", "Box " + id);
        box.set("speed", boxCapacity[0]);
        box.set("core", Integer.parseInt(boxCapacity[1]));
        box.set("img", "Box");
        box.set("level", popLevel + 2);

        boxIds.put(id, boxId);

        int pop = id % lowPopIds.size() + highPopCount;
        int edgeIndex = graph.addEdge(boxId, pop);
        Edge edge = graph.getEdge(edgeIndex);
        edge.set("link", house_link);
        edge.set("state", 0);

        AggregateItem aggItem = (AggregateItem) aggTable.addItem();
        aggItems.add(aggItem);
        aggItem.setInt("id", id);
        aggItem.addItem(vis.getVisualItem(GRAPH, box));

        NodeList nodeList = e.getElementsByTagName("pc");
        for (int i = 0; i < nodeList.getLength(); i++) {
            CreatePC((Element) nodeList.item(i), i, id, box);
        }
    }

    private void CreatePC(Element e, int index, int house, Node box) {
        String capacity[] = e.getFirstChild().getNodeValue().split(" ");

        Node n = graph.addNode();
        n.set("name", "H" + house + " : PC " + index);
        n.set("speed", capacity[0]);
        n.set("core", Integer.parseInt(capacity[1]));
        n.set("img", "PC");
        n.set("level", popLevel + 3);

        aggItems.get(house).addItem(vis.getVisualItem(GRAPH, n));

        Edge edge = graph.addEdge(n, box);
        edge.set("link", wifi_link);
        edge.set("state", 0);
    }

    private void CreateMobile(Element e) {
        int id = Integer.parseInt(e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue());
        String capacity[] = e.getElementsByTagName("capacity").item(0).getFirstChild().getNodeValue().split(" ");

        int i = graph.addNodeRow();
        Node n = graph.getNode(i);
        n.set("name", "Mobile " + id);
        n.set("speed", capacity[0]);
        n.set("core", Integer.parseInt(capacity[1]));
        n.set("img", "Mobile");
        n.set("level", popLevel + 1);

        int bsId = bsIds.get(id % bsIds.size());
        Edge edge = graph.addEdge(n, graph.getNode(bsId));
        edge.set("link", mobile_link);
        edge.set("state", 0);

        mobileUpdaters.put(id, new MobileUpdater(i, bsId));
    }

    private int GetPopIndex(String s) {
        if (s.startsWith("pop")) {
            return Integer.parseInt(s.substring(3));
        }
        return -1;
    }

    public static void UpdateMobiles() {
        for (MobileUpdater updater : mobileUpdaters.values()) {
            if (random.nextInt(10) < 2) // update
            {
                if (random.nextBoolean()) // bs
                {
                    int bs = bsIds.get(random.nextInt(bsIds.size()));
                    updater.UpdateConnect(bs, -1);
                } else // house
                {
                    int house = random.nextInt(boxIds.keySet().size());
                    updater.UpdateConnect(boxIds.get(house), house);
                }
            }
        }

        Iterator iterator = graph.getEdges().tuples();
        while (iterator.hasNext()) {
            Tuple t = (Tuple) iterator.next();

            if (random.nextInt(100) < 80) {
                t.set("state", 0);
            } else if (random.nextInt(100) < 80) {
                t.set("state", 1);
            } else {
                t.set("state", 2);
            }
        }
    }

    public static Graph getGraph() {
        return graph;
    }

    public static Visualization getVis() {
        return vis;
    }

    class MobileUpdater {
        int deviceId = -1;
        int edgeTarget = -1;
        int preHouse = -1;

        public MobileUpdater(int deviceId, int edgeTarget) {
            this.deviceId = deviceId;
            this.edgeTarget = edgeTarget;
        }

        public boolean UpdateConnect(int newConnect, int house) {
            if (edgeTarget == newConnect) {
                return false;
            }

            int preEdge = graph.getEdge(deviceId, edgeTarget);
            graph.removeEdge(preEdge);

            int newEdge = graph.addEdge(deviceId, newConnect);
            Edge edge = graph.getEdge(newEdge);

            Node mobile = graph.getNode(deviceId);

            if (house < 0) {
                edge.set("link", mobile_link);
                edge.set("state", 0);

                if (preHouse >= 0) {
                    aggItems.get(preHouse).removeItem(vis.getVisualItem(GRAPH, mobile));
                }
            } else {
                edge.set("link", wifi_link);
                if (preHouse != -1) {
                    aggItems.get(preHouse).removeItem(vis.getVisualItem(GRAPH, mobile));
                }

                aggItems.get(house).addItem(vis.getVisualItem(GRAPH, mobile));
            }

            edgeTarget = newConnect;
            preHouse = house;

            return true;
        }

        public int getDeviceId() {
            return deviceId;
        }
    }
}
