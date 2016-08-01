/*
 * MessageStoreViewer.java
 *
 * Created on 28 November 2007, 09:04
 */

package uk.co.sleonard.unison.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import uk.co.sleonard.unison.UNISoNControllerFX;
import uk.co.sleonard.unison.UNISoNException;
import uk.co.sleonard.unison.UNISoNLogger;
import uk.co.sleonard.unison.datahandling.DAO.DownloadRequest.DownloadMode;
import uk.co.sleonard.unison.datahandling.DAO.GUIItem;
import uk.co.sleonard.unison.datahandling.DAO.Location;
import uk.co.sleonard.unison.datahandling.DAO.Message;
import uk.co.sleonard.unison.datahandling.DAO.NewsGroup;
import uk.co.sleonard.unison.datahandling.DAO.ResultRow;
import uk.co.sleonard.unison.datahandling.DAO.Topic;
import uk.co.sleonard.unison.datahandling.DAO.UsenetUser;
import uk.co.sleonard.unison.input.FullDownloadWorker;
import uk.co.sleonard.unison.utils.StringUtils;
import uk.co.sleonard.unison.utils.TreeNode;

/**
 * The class MessageStoreViewer, Controller of the Tab View Saved Data.
 *
 * @author Stephen <github@leonarduk.com> and adapted to JavaFX by Elton
 *         <elton_12_nunes@hotmail.com>
 * @since 28-Jun-2016
 */
public class MessageStoreViewerFX implements Observer, UNISoNLogger {

	/** -------------------- Components Variables ------------------ */
	/** The body pane. */
	@FXML
	private TextArea bodyPane;
	/** The body scroll pane. */
	@FXML
	private ScrollPane bodyScrollPane;
	/** The crosspost combo box. */
	@FXML
	private ComboBox<NewsGroup> crosspostComboBox;
	/** The filter toggle. */
	@FXML
	private ToggleButton filterToggle;
	/** The from date field. */
	@FXML
	private TextField fromDateField;
	/** The from date label. */
	@FXML
	private Label fromDateLabel;
	/** The get body button. */
	// private javax.swing.JButton getBodyButton; Maybe no implemented yet
	/** The groups hierarchy. */
	@FXML
	private TreeView<Object> groupsHierarchy;
	/** The groups scroll pane. */
	@FXML
	private ScrollPane groupsScrollPane;
	/** The headers button. */
	@FXML
	private Button headersButton;
	/** The location field. */
	@FXML
	private TextField locationField;
	/** The location label. */
	@FXML
	private Label locationLabel;
	/** The missing messages check. */
	// private javax.swing.JCheckBox missingMessagesCheck; Maybe no implemented
	// yet
	/** The refresh button. */
	@FXML
	private Button refreshButton;
	/** The sender field. */
	@FXML
	private TextField senderField;
	/** The sender label. */
	@FXML
	private Label senderLabel;
	/** The sent date field. */
	@FXML
	private TextField sentDateField;
	/** The sent date label. */
	@FXML
	private Label sentDateLabel;
	/** The stats tab pane. */
	// private javax.swing.JTabbedPane statsTabPane; Maybe no implemented yet
	/** The subject field. */
	@FXML
	private TextField subjectField;
	/** The subject label. */
	@FXML
	private Label subjectLabel;
	/** The to date field. */
	@FXML
	private TextField toDateField;
	/** The todate label. */
	@FXML
	private Label toDateLabel;
	/** The top countries list. */
	@FXML
	private ListView<GUIItem<Object>> topCountriesList;
	/** The top countries scroll pane. */
	@FXML
	private ScrollPane topCountriesScrollPane;
	/** The top groups list. */
	@FXML
	private ListView<GUIItem<Object>> topGroupsList;
	/** The top groups scroll pane. */
	@FXML
	private ScrollPane topGroupsScrollPane;
	/** The top posters list. */
	@FXML
	private ListView<GUIItem<Object>> topPostersList;
	/** The top posters scroll pane. */
	@FXML
	private ScrollPane topPostersScrollPane;
	/** The topics hierarchy. */
	@FXML
	private TreeView<Object> topicsHierarchy;
	/** The topics scroll pane. */
	@FXML
	private ScrollPane topicsScrollPane;
	/** The session. */
	private Session session;
	/** The newsgroup tree root. */
	private TreeNode newsgroupTreeRoot;
	/** The topic root. */
	private TreeNode topicRoot;

	/** END----------------- Components Variables ---------------END */

	/**
	 * Creates new form MessageStoreViewer.
	 */
	public MessageStoreViewerFX() {
	}

	/**
	 * The initialize() method is called (if it is present) after the loading of
	 * the scene graph is complete (so all the GUI objects will have been
	 * instantiated) but before control has returned to your application's
	 * invoking code.
	 * 
	 */
	@FXML
	private void initialize() {
		try {

			this.topPostersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			this.topCountriesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			this.topGroupsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			this.session = UNISoNControllerFX.getInstance().getHelper().getHibernateSession();

			// FIXME disable all non-workng parts
			// headersButton.setVisible(false);
			// this.getBodyButton.setVisible(false);
			// this.missingMessagesCheck.setVisible(false);

			this.switchFilter(this.filterToggle.isSelected());

			this.newsgroupTreeRoot = new TreeNode(null, "NewsGroups");
			this.topicRoot = new TreeNode(null, "Topics");

			// Add Event in groupsHierarchy
			EventHandler<MouseEvent> mouseEventHandleNewsGroup = (MouseEvent event) -> {
				handleMouseClickedNewsGroup(event);
			};
			this.groupsHierarchy.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandleNewsGroup);
			// ----------------------------

			// Add Event in topicsHierarchy
			EventHandler<MouseEvent> mouseEventHandleTopics = (MouseEvent event) -> {
				mouseEventHandleTopics(event);
			};
			this.topicsHierarchy.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandleTopics);
			// ----------------------------

			UNISoNControllerFX.getInstance().getDatabase().addObserver(this);
			// After update to JavaFX the PajekPanel change this.
			UNISoNControllerFX.getInstance().getDatabase().refreshDataFromDatabase();
		} catch (final UNISoNException e) {
			UNISoNControllerFX.getInstance();
			UNISoNControllerFX.getGui().showAlert("Error :" + e.getMessage());
		}
	}

	/**
	 * Event of mouse clicked in Node into groupsHierarchy(TreeView) Groups
	 * hierarchy value changed.
	 * 
	 * @param event
	 *            Mouse event.
	 */
	private void handleMouseClickedNewsGroup(MouseEvent event) {
		Node node = event.getPickResult().getIntersectedNode();
		// Accept clicks only on node cells, and not on empty spaces of the
		// TreeView
		if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
			Object item = ((TreeItem) this.groupsHierarchy.getSelectionModel().getSelectedItem()).getValue();
			// Assure that is a item.
			if (item instanceof NewsGroup) {
				UNISoNControllerFX.getInstance().getFilter().setSelectedNewsgroup((NewsGroup) item);
			} else {
				if (item instanceof String) {
					UNISoNControllerFX.getInstance().getFilter().setSelectedNewsgroup((String) item);
				}
			}

			this.notifySelectedNewsGroupObservers();
		}
	}

	/**
	 * Event of mouse clicked in Node into topicsHierarchy(TreeView) Topics
	 * hierarchy value changed.
	 * 
	 * @param event
	 *            Mouse event
	 */
	private void mouseEventHandleTopics(MouseEvent event) {
		Node node = event.getPickResult().getIntersectedNode();

		if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
			Object item = ((TreeItem) this.topicsHierarchy.getSelectionModel().getSelectedItem()).getValue();
			if (item instanceof Message) {
				final Message msg = (Message) item;
				UNISoNControllerFX.getInstance().getFilter().setSelectedMessage(msg);
				this.notifySelectedMessageObservers();
			} else {
				TreeNode selectedItem = (TreeNode) (TreeItem) this.topicsHierarchy.getSelectionModel()
						.getSelectedItem();
				if (selectedItem.getChildren().size() == 0) { // Grant no
																// children
																// duplicate.
					this.expandNode(selectedItem);
				}
			}
			this.notifySelectedMessageObservers();
		}

	}

	@FXML
	private void mouseEventHandleCrosspost() {
		final UNISoNControllerFX controller = UNISoNControllerFX.getInstance();
		final NewsGroup selectedGroup = this.crosspostComboBox.getSelectionModel().getSelectedItem();
		controller.getFilter().setSelectedNewsgroup(selectedGroup);
		this.refreshTopicHierarchy();

	}

	/**
	 * Adds the child node.
	 *
	 * @param root
	 *            the root
	 * @param childObject
	 *            the child object
	 * @return the tree node
	 */
	protected TreeNode addChildNode(final TreeNode root, final Object childObject) {
		return this.addChildNode(root, childObject, "");
	}

	/**
	 * Adds the child node.
	 *
	 * @param root
	 *            the root
	 * @param childObject
	 *            the child object
	 * @param name
	 *            the name
	 * @return the tree node
	 */
	protected TreeNode addChildNode(final TreeNode root, final Object childObject, final String nameInput) {
		String name = nameInput;
		if (childObject instanceof Set<?>) {
			if (((Set<?>) childObject).size() == 0) {
				// if no entries then don't add it
				return null;
			}
		} else if (childObject instanceof String) {
			name += " : " + childObject;
		} else {
			name += UNISoNControllerFX.getInstance().getHelper().getText(childObject);
		}

		final TreeNode child = new TreeNode(childObject, name);
		root.getChildren().add(child);

		return child;
	}

	@Override
	public void alert(final String message) {
		this.log(message);
		UNISoNControllerFX.getInstance();
		UNISoNControllerFX.getGui().showAlert(message);
	}

	/**
	 * Creates the message hierarchy.
	 *
	 * @param set
	 *            the set
	 * @param root
	 *            the root
	 * @param matchId
	 *            the match id
	 * @param fillInMissing
	 *            the fill in missing
	 * @return the sets the
	 */
	private Set<Message> createMessageHierarchy(final Set<Message> set, final TreeNode root, final Object matchId,
			final boolean fillInMissing) {
		final Set<TreeNode> matches = new HashSet<>();
		final Set<Message> copy = new HashSet<>(set);

		for (final Message next : set) {
			// compare to the last refered message, ie. the one they replied to
			String previousId = "ROOT";
			try {
				final List<String> msgList = StringUtils.convertStringToList(next.getReferencedMessages(), " ");

				if (msgList.size() > 0) {
					final String lastMessageId = msgList.get(0);
					if (fillInMissing) {
						previousId = lastMessageId;

					}
					// else ignore it and add to root
				}
			} catch (final ObjectNotFoundException e) {
				e.printStackTrace();
			}

			// if it matches then it refers to previous so add as a child to
			// previous
			if (previousId.equals(matchId)) {
				final TreeNode child = this.addChildNode(root, next);
				matches.add(child);
				copy.remove(next);
			}
		}
		Set<Message> remainder = new HashSet<>(copy);
		for (final TreeNode next : matches) {
			remainder = this.createMessageHierarchy(remainder, next, ((Message) next.getValue()).getUsenetMessageID(),
					fillInMissing);
		}
		return copy;
	}

	/**
	 * Expand node.
	 *
	 * @param root
	 *            the root
	 * @param fillInMissing
	 *            the fill in missing
	 */
	protected void expandNode(final TreeNode root) {

		final Object userObject = root.getValue();
		if (userObject instanceof Topic) {
			final Topic topic = (Topic) userObject;
			this.createMessageHierarchy(UNISoNControllerFX.getInstance().getDatabase().getMessages(topic, this.session),
					root, "ROOT", false);
		}
	}

	/**
	 * Filter toggle action performed.
	 *
	 */
	@FXML
	private void filterToggle() {
		try {
			this.switchFilter(this.filterToggle.isSelected());
		} catch (final UNISoNException e) {
			UNISoNControllerFX.getInstance();
			UNISoNControllerFX.getGui().showAlert("Error :" + e.getMessage());
		}
	}

	/**
	 * Gets the list model.
	 *
	 * @param results
	 *            the results
	 * @return the list model
	 */
	private ObservableList<GUIItem<Object>> getListModel(final List<ResultRow> results) {
		final List<GUIItem<Object>> model = new ArrayList<>();
		for (final ListIterator<ResultRow> iter = results.listIterator(); iter.hasNext();) {
			final Object next = iter.next();
			String name = next.toString();
			if (next instanceof UsenetUser) {
				name = ((UsenetUser) next).getName() + "<" + ((UsenetUser) next).getEmail() + ">";
			} else if (next instanceof Location) {
				name = ((Location) next).getCountry();
			}

			model.add(new GUIItem<>(name, next));
		}
		return FXCollections.observableArrayList(model);
	}

	/**
	 * Headers button action performed.
	 *
	 */
	@FXML
	private void headersButton() {
		try {
			for (final Message message : UNISoNControllerFX.getInstance().getFilter().getMessagesFilter()) {
				// only download for messages that need it
				if (null == message.getPoster().getLocation()) {
					FullDownloadWorker.addDownloadRequest(message.getUsenetMessageID(), DownloadMode.HEADERS,
							UNISoNControllerFX.getInstance().getDownloadPanel());
				}
			}
		} catch (final UNISoNException e) {
			this.alert("Failed to download extra fields: " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.co.sleonard.unison.gui.UNISoNLogger#log(java.lang.String)
	 */
	@Override
	public void log(final String message) {
		// notesArea.append(message + "\n");
	}

	/**
	 * Notify selected message observers.
	 */
	public void notifySelectedMessageObservers() {
		this.refreshMessagePane();
	}

	/**
	 * Notify selected news group observers.
	 */
	public void notifySelectedNewsGroupObservers() {
		this.refreshTopicHierarchy();
	}

	/**
	 * Refresh button action performed.
	 *
	 */
	@FXML
	private void refreshDataButton() {
		final UNISoNControllerFX controller = UNISoNControllerFX.getInstance();
		controller.getDatabase().refreshDataFromDatabase();
	}

	/**
	 * Key method - this refreshes all the GUI components with fresh data from
	 * the database.
	 */
	public void refreshGUIData() {
		// this.refreshTopPostersTable();

		this.refreshMessagePane();
		this.refreshTopicHierarchy();
		this.refreshNewsGroupHierarchy();

		this.refreshTopCountries();
		this.refreshTopPosters();
		this.refreshTopGroups();
	}

	/**
	 * Refresh message pane.
	 */
	public void refreshMessagePane() {
		final Message message = UNISoNControllerFX.getInstance().getFilter().getSelectedMessage();

		if (null != message) {

			String subject = message.getSubject();
			if (subject.length() > 18) {
				subject = subject.substring(0, 15) + "...";
			}
			this.subjectField.setText(subject);
			this.subjectField.setTooltip(new Tooltip(message.getSubject()));

			String name2 = message.getPoster().getName();
			if (name2.length() > 18) {
				name2 = name2.substring(0, 15) + "...";
			}
			this.senderField.setText(name2);
			this.senderField.setTooltip(new Tooltip(message.getPoster().toString()));

			String location;
			String fullLocation;
			if (null == message.getPoster().getLocation()) {
				location = "UNKNOWN";
				fullLocation = "Download header to get location";
			} else {
				location = message.getPoster().getLocation().toString();
				fullLocation = message.getPoster().getLocation().fullString();
			}

			this.locationField.setText(location);
			this.locationField.setTooltip(new Tooltip(fullLocation));

			this.sentDateField.setText(new SimpleDateFormat("dd MMM yyyy hh:mm").format(message.getDateCreated()));
			ObservableList<NewsGroup> aList = FXCollections
					.observableList(new ArrayList<NewsGroup>(message.getNewsgroups()));
			this.crosspostComboBox.setItems(aList);
			try {
				this.bodyPane.setText(StringUtils.decompress(message.getMessageBody()));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Refresh news group hierarchy.
	 */
	protected void refreshNewsGroupHierarchy() {
		this.newsgroupTreeRoot.getChildren().clear(); // this.newsgroupTreeRoot.removeAllChildren();
		// FIXME split out from name - ignore db stuff
		final UNISoNControllerFX controller = UNISoNControllerFX.getInstance();
		final HashMap<String, TreeNode> nodeMap = new HashMap<>();

		final List<NewsGroup> newsgroupFilter = new ArrayList<>();
		newsgroupFilter.addAll(controller.getFilter().getNewsgroupFilter());
		Collections.sort(newsgroupFilter);

		for (final NewsGroup group : newsgroupFilter) {
			final String[] nameparts = group.getFullName().split("\\.");
			String pathSoFar = "";
			TreeNode parent = this.newsgroupTreeRoot;

			for (final String namePart : nameparts) {
				if (!pathSoFar.equals("")) {
					pathSoFar += ".";
				}
				pathSoFar += namePart;
				TreeNode node = nodeMap.get(pathSoFar);

				Object data = pathSoFar;
				if (namePart.equals(group.getName())) {
					// base part
					data = group;
				}

				if (null == node) {
					node = new TreeNode(data, namePart);
					parent.getChildren().add(node);
					node.getParent().setValue(parent);
					nodeMap.put(pathSoFar, node);
				} else {
					// If node created by earlier newsgroup
					if (node.getValue() instanceof String) {
						node.setValue(data);
					}
				}
				// ready for next iteration
				parent = node;
			}
		}
		// Set<NewsGroup> topNewsGroups = controller.getTopNewsGroups();
		// Set<NewsGroup> newsgroupsFilter = controller.getNewsgroupFilter();
		// Set<NewsGroup> groups = new HashSet<NewsGroup>();
		// final Iterator<NewsGroup> iter = topNewsGroups.iterator();
		// while (iter.hasNext()) {
		// final NewsGroup group = iter.next();
		// boolean addGroup = true;
		// if (null != newsgroupsFilter) {
		// addGroup = false;
		// for (Iterator<NewsGroup> iter2 = newsgroupsFilter.iterator();
		// !addGroup
		// && iter2.hasNext();) {
		// String fullName = iter2.next().getFullName();
		// int indexOf = fullName.indexOf(".");
		// String topLevel = fullName;
		// if (indexOf > -1) {
		// topLevel = fullName.substring(0, indexOf);
		// }
		// if (group.getFullName().equals(topLevel)) {
		// addGroup = true;
		// continue;
		// }
		// }
		// }
		// if (addGroup && !groups.contains(group)) {
		// groups.add(group);
		// }
		// }
		// for (NewsGroup group : groups) {
		// this.addChildNode(this.newsgroupTreeRoot, group);
		// }
		// This actually refreshes the tree
		this.groupsHierarchy.setRoot(this.newsgroupTreeRoot);
		this.groupsHierarchy.refresh();
	}

	/**
	 * Refresh top posters.
	 */
	private void refreshTopPosters() {
		final Vector<ResultRow> results = UNISoNControllerFX.getInstance().getAnalysis().getTopPosters();

		this.topPostersList.setItems(this.getListModel(results));
	}

	/**
	 * Refresh top countries.
	 */
	private void refreshTopCountries() {
		final List<ResultRow> results = UNISoNControllerFX.getInstance().getAnalysis().getTopCountriesList();

		this.topCountriesList.setItems(this.getListModel(results));

	}

	/**
	 * Refresh top groups.
	 */
	private void refreshTopGroups() {
		final List<ResultRow> results = UNISoNControllerFX.getInstance().getAnalysis().getTopGroupsList();

		this.topGroupsList.setItems(this.getListModel(results));
	}

	/**
	 * Refresh topic hierarchy.
	 */
	private void refreshTopicHierarchy() {
		// TODO reinstate that topics reflect the highlighted newsgroup

		this.topicRoot.getChildren().clear();

		final UNISoNControllerFX controller = UNISoNControllerFX.getInstance();
		final NewsGroup selectedNewsgroup = controller.getFilter().getSelectedNewsgroup();
		if (null != selectedNewsgroup) {
			this.topicRoot.setName(selectedNewsgroup.getFullName());
			final Set<Topic> topics = selectedNewsgroup.getTopics();
			final Set<Topic> topicsFilter = controller.getFilter().getTopicsFilter();
			for (final Topic topic : topics) {
				if ((null == topicsFilter) || topicsFilter.contains(topic)) {
					final int lastIndex = topic.getSubject().length();
					this.addChildNode(this.topicRoot, topic, topic.getSubject().substring(0, lastIndex));
				}
			}

		} else {
			this.topicRoot.setName("No group selected");
		}

		// This actually refreshes the tree
		this.topicsHierarchy.setRoot(this.topicRoot);
		this.topicsHierarchy.refresh();
	}

	/**
	 * Switch filter.
	 *
	 * @param on
	 *            the on
	 * @throws UNISoNException
	 *             the UNI so n exception
	 */
	@SuppressWarnings("unchecked")
	private void switchFilter(final boolean on) throws UNISoNException {
		try {
			final UNISoNControllerFX controller = UNISoNControllerFX.getInstance();

			if (on) {
				final Date fromDate = StringUtils.stringToDate(this.fromDateField.getText());
				final Date toDate = StringUtils.stringToDate(this.toDateField.getText());
				controller.getFilter().setDates(fromDate, toDate);

				final List<GUIItem<Object>> selectedCountries = this.topCountriesList.getSelectionModel()
						.getSelectedItems();
				final Set<String> countries = new HashSet<>();
				for (final Object country : selectedCountries) {
					final GUIItem<ResultRow> row = (GUIItem<ResultRow>) country;
					final String selectedcountry = (String) row.getItem().getKey();
					countries.add(selectedcountry);
				}
				controller.getFilter().setSelectedCountries(countries);

				final List<GUIItem<Object>> selectedNewsgroups = this.topGroupsList.getSelectionModel()
						.getSelectedItems();
				final Vector<NewsGroup> groups = new Vector<>();
				for (final Object group : selectedNewsgroups) {
					final GUIItem<ResultRow> row = (GUIItem<ResultRow>) group;
					final NewsGroup selectedgroup = (NewsGroup) row.getItem().getKey();
					groups.add(selectedgroup);
				}
				controller.getFilter().setSelectedNewsgroups(groups);

				final List<GUIItem<Object>> selectedPosters = this.topPostersList.getSelectionModel()
						.getSelectedItems();
				final Vector<UsenetUser> posters = new Vector<>();
				for (final Object poster : selectedPosters) {
					final GUIItem<ResultRow> row = (GUIItem<ResultRow>) poster;
					final UsenetUser selectedUser = (UsenetUser) row.getItem().getKey();
					posters.add(selectedUser);
				}
				controller.getFilter().setSelectedPosters(posters);
				this.filterToggle.setText("Filtered");
				this.filterToggle.setTooltip(new Tooltip("Click again to remove filter"));
				this.refreshButton.setDisable(true);
			} else {
				this.filterToggle.setText("Filter");
				this.filterToggle.setTooltip(new Tooltip(
						"Enter date values, select groups or posters in lists, or combination then click filter"));
				this.refreshButton.setDisable(false);
			}
			this.fromDateField.setEditable(!on);
			this.toDateField.setEditable(!on);
			controller.switchFiltered(on);
		} catch (final DateTimeParseException e) {
			this.alert("Failed to parse date : " + e.getMessage());
			this.filterToggle.setSelected(false);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(final Observable observable, final Object arg1) {
		this.refreshGUIData();
	}

}
