package main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Panel;
import javax.swing.JList;
import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class SearchPerson extends JFrame {
	public static final String SOURCE = "./resources/ontologies/";
	public static final String OMDb = "http://www.semanticweb.org/edison/ontologies/2019/10/OMdB_Semantic#";

	private JPanel contentPane;
	private JComboBox<String> menu;
	private JLabel lblAName = new JLabel(".......");
	private JLabel imdbID = new JLabel("...");
	private JList<String> mediaList = new JList<String>();

	OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
	String prefix = "";
	private JScrollPane sp = new JScrollPane();
	public static SearchPerson castsFrame = new SearchPerson();
	private JLabel lbl_imdbID;

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					castsFrame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Creating the frame
	public SearchPerson() {
		setFont(new Font("Arial", Font.PLAIN, 14));
		setIconImage(Toolkit.getDefaultToolkit().getImage("./resources/images/semantic.png"));
		setTitle("Semantic Media Search");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setPreferredSize(new Dimension(800, 800));
		setMaximumSize(new Dimension(800, 800));
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(700, 700));
		contentPane.setMaximumSize(new Dimension(400, 400));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		// read ontology model
		FileManager.get().readModel(m, SOURCE + "OMdB_Semantic.owl");

		prefix = "prefix rdfs: <" + RDFS.getURI() + ">\n" + "prefix owl: <" + OMDb + ">\n";

		// sparql to read people
		String query_text = prefix + "SELECT ?aName " + "WHERE { ?p a owl:Person." + "?p owl:name ?aName \r\n"
				+ "} ORDER BY ASC(?aName)";
		Query query = QueryFactory.create(query_text);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);

		menu = new JComboBox<String>();

		JButton btnSearchPeople = new JButton("Return");
		btnSearchPeople.setFocusTraversalKeysEnabled(false);
		btnSearchPeople.setFocusPainted(false);
		btnSearchPeople.setBackground(SystemColor.controlHighlight);
		btnSearchPeople.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnSearchPeople.setBounds(20, 20, 350, 45);
		btnSearchPeople.setPreferredSize(new Dimension(25, 100));
		btnSearchPeople.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new mainMenu().setVisible(true);
				castsFrame.dispose();
			}
		});
		contentPane.add(btnSearchPeople);
		
		// loads casts into dropbox
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				menu.addItem(qs.get("aName").toString());
			}

		} finally {
			qexec.close();
		}

		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					String aName = menu.getSelectedItem().toString();
					lblAName.setText(aName);

					// retrieving casts's imdbID & etc
					String query_text = prefix + "SELECT ?name ?imdbID "
							+ "(GROUP_CONCAT(DISTINCT str(?mTitle);separator=';') AS ?medias)"
							+ "WHERE {?p a owl:Person." + "?p owl:name ?name." + "?p owl:imdbID ?imdbID." + "OPTIONAL{"
							+ "{?p owl:isActorOf ?m." + "?m owl:Title ?mTitle.}" + "UNION" + "{?m owl:hasActor ?p."
							+ "?p owl:name ?name}}" + " FILTER(str(?name)=\"" + aName + "\")" + "}"
							+ " GROUP BY ?name ?imdbID ?mTitle";

					Query query = QueryFactory.create(query_text);
					QueryExecution qexec = QueryExecutionFactory.create(query, m);

					try {
						ResultSet results = qexec.execSelect();
						DefaultListModel<String> listModel = new DefaultListModel<String>();
						while (results.hasNext()) {
							QuerySolution qs = results.next();
							if (qs.get("medias").toString() != null){
								String[] mediaList = qs.get("medias").split(";");
							for (int i = 0; i < mediaList.length; i++) {
								System.out.println("-----" + mediaList[i]);
								listModel.addElement(mediaList[i].toString());
							}}
							imdbID.setText(qs.get("imdbID").toString());
						}
						mediaList.setModel(listModel);
					}catch(NullPointerException e1) {
						e1.getMessage();
						System.out.println("test");
					} finally {
						qexec.close();
					}
				}

			}
		});
		contentPane.setLayout(null);

		// label for selecting person
		JLabel lblSelectA = new JLabel("Select Person:");
		lblSelectA.setFont(new Font("Cambria", Font.BOLD, 36));
		lblSelectA.setForeground(new Color(0, 128, 128));
		lblSelectA.setBounds(50, 74, 264, 45);
		contentPane.add(lblSelectA);

		menu.setBackground(UIManager.getColor("ComboBox.buttonBackground"));
		menu.setForeground(SystemColor.desktop);
		menu.setBounds(291, 81, 305, 36);
		contentPane.add(menu);

		Panel panel = new Panel();
		panel.setBounds(24, 169, 644, 486);
		contentPane.add(panel);
		panel.setLayout(null);
		imdbID.setForeground(new Color(70, 130, 180));
		imdbID.setBounds(80, 17, 225, 40);

		// label for person's name
		lblAName.setForeground(new Color(70, 130, 180));
		lblAName.setBounds(12, 17, 225, 40);
		panel.add(lblAName);

		lblAName.setFont(new Font("Georgia", Font.BOLD, 25));

		// label for imdbID of the person
		lbl_imdbID = new JLabel("IMdB ID:");
		lbl_imdbID.setFont(new Font("Tahoma", Font.BOLD, 17));
		lbl_imdbID.setBounds(12, 86, 140, 26);
		panel.add(lbl_imdbID);
		imdbID.setFont(new Font("Tahoma", Font.PLAIN, 17));

		imdbID.setBounds(164, 86, 140, 26);
		panel.add(imdbID);

		// casted in which media
		//mediaList not showing, but has element in it
		JLabel lblMediaList = new JLabel("Casted In:");
		lblMediaList.setFont(new Font("Cambria", Font.BOLD, 17));
		lblMediaList.setBounds(12, 180, 81, 40);
		panel.add(lblMediaList);
		lblMediaList.setSize(new Dimension(350, 300));
		mediaList.setModel(new AbstractListModel() {
			String[] values = new String[] {};

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		mediaList.setFont(new Font("Times New Roman", Font.PLAIN, 17));
		mediaList.setBounds(164, 232, 277, 200);
		panel.add(mediaList);
	}
}