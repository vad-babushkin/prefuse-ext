DROP DATABASE nodedges;

CREATE DATABASE nodedges;

USE nodedges;


--
-- Table structure for table `nodes`
--

DROP TABLE IF EXISTS `nodes`;
CREATE TABLE `nodes` (
  `nid` int(32) NOT NULL,
  `name` longtext default NULL,
  `acronym` longtext default NULL,
  `location` longtext default NULL,
  `country` longtext default NULL,
  `website` longtext default NULL,
  `contact` longtext default NULL,
  `description` longtext default NULL,
  `type` longtext default NULL,
  `image` longtext default NULL,
  `theme` longtext default NULL,
  `groups` longtext default NULL,
  `esf1` longtext default NULL,
  `esf2` longtext default NULL,
  `esf3` longtext default NULL
) TYPE=MyISAM;

--
-- Table structure for table `edges`
--

DROP TABLE IF EXISTS `edges`;
CREATE TABLE `edges` (
  `source` int(32) NOT NULL,
  `target` int(32) NOT NULL,
  `type` longtext default NULL
) TYPE=MyISAM;
