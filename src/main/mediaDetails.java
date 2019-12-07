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
public class mediaDetails extends JFrame {

	// Directory where we've stored the local data files, such as places.owl
	public static final String SOURCE = "./resources/ontologies/";
	public static final String OMDb = "http://www.semanticweb.org/edison/ontologies/2019/10/OMdB_Semantic#";

	private JPanel contentPane;
	private JComboBox<String> menu;
	private JLabel mediaTitle = new JLabel(".......");
	private JLabel imdbID = new JLabel("...");
	private JLabel plot = new JLabel("...");
	private JList<String> castsList = new JList<String>();
	private JLabel picture = new JLabel("");

	OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
	String prefix = "";

	private JScrollPane sp = new JScrollPane();

	public static mediaDetails mediaFrame = new mediaDetails();

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mediaFrame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public mediaDetails() {
		setFont(new Font("Arial", Font.PLAIN, 14));
		setIconImage(Toolkit.getDefaultToolkit().getImage("./resources/images/semantic.png"));
		setTitle("Semantic Media Search");
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

		// sparql to read movie title
		String query_text = prefix + "SELECT ?mTitle " + "	WHERE { ?m a owl:Media." + "?m owl:Title ?mTitle \r\n"
				+ "} ORDER BY ASC(?mTitle)";

		Query query = QueryFactory.create(query_text);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);

		menu = new JComboBox<String>();

		// Return to main menu
		JButton btnReturn = new JButton("Return");
		btnReturn.setFocusTraversalKeysEnabled(false);
		btnReturn.setFocusPainted(false);
		btnReturn.setBackground(SystemColor.controlHighlight);
		btnReturn.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnReturn.setBounds(20, 20, 350, 45);
		btnReturn.setPreferredSize(new Dimension(25, 100));
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new mainMenu().setVisible(true);
				mediaFrame.dispose();
			}
		});
		contentPane.add(btnReturn);

		// loads media into dropbox
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				menu.addItem(qs.get("mTitle").toString());
			}

		} finally {
			qexec.close();
		}

		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					String mTitle = menu.getSelectedItem().toString();
					mediaTitle.setText(mTitle);
					// retrieving media's plot, actor, etc

					String query_text = prefix +
					// not getting actors name or plot
					"SELECT ?mTitle ?imdbID ?plot" + "(GROUP_CONCAT(DISTINCT str(?name);separator=';') AS ?casts)"
							+ "WHERE {?m a owl:Media." + "?m owl:Title ?mTitle." + "?m owl:Plot ?plot."
							+ "?m owl:imdbID ?imdbID."

							+ "OPTIONAL{" + "{?m owl:hasActor ?a." + "?a owl:name ?name.}" + "UNION"
							+ "{?a owl:isActorOf ?m." + "?a owl:name ?name}} "

							+ "OPTIONAL{{?m owl:hasDirector? ?d." + "?d owl:name ?name.}" + "UNION"
							+ "{?d owl:isDirectorOf ?m." + "?d owl:name ?name}} "

							+ "OPTIONAL{{?m owl:hasVA ?va." + "?va owl:name ?name.}" + "UNION" + "{?va owl:isVAOf ?m."
							+ "?va owl:name ?name}} "

							+ "OPTIONAL{{?m owl:hasCreator ?c." + "?c owl:name ?name.}" + "UNION"
							+ "{?c owl:isCreatorOf ?m." + "?c owl:name ?name}} "

							+ "OPTIONAL{{?m owl:hasWriter ?w." + "?w owl:name ?name.}" + "UNION"
							+ "{?w owl:isWriterOf ?m." + "?w owl:name ?name}} "

							+ "FILTER(str(?mTitle)=\"" + mTitle + "\")" + "}" + " GROUP BY ?mTitle ?imdbID ?plot ?name";

					Query query = QueryFactory.create(query_text);
					QueryExecution qexec = QueryExecutionFactory.create(query, m);

					try {
						ResultSet results = qexec.execSelect();
						DefaultListModel<String> listModel = new DefaultListModel<String>();
						while (results.hasNext()) {
							QuerySolution qs = results.next();
							String[] castsList = qs.get("casts").toString().split(";");
							for (int i = 0; i < castsList.length; i++) {
								listModel.addElement(castsList[i].toString());
							}
							imdbID.setText(qs.get("imdbID").toString());
							plot.setText("<html>" + qs.get("plot").toString() + "</html>");
						}
						castsList.setModel(listModel);
					} finally {
						qexec.close();
					}
				}

			}
		});
		contentPane.setLayout(null);

		JLabel lblSMedia = new JLabel("Select Media:");
		lblSMedia.setFont(new Font("Cambria", Font.BOLD, 36));
		lblSMedia.setForeground(new Color(0, 128, 128));
		lblSMedia.setBounds(50, 74, 264, 45);
		contentPane.add(lblSMedia);

		menu.setBackground(UIManager.getColor("ComboBox.buttonBackground"));
		menu.setForeground(SystemColor.desktop);
		menu.setBounds(291, 81, 305, 36);
		contentPane.add(menu);

		Panel panel = new Panel();
		panel.setBounds(24, 169, 644, 486);
		contentPane.add(panel);
		panel.setLayout(null);
		plot.setForeground(new Color(70, 130, 180));
		plot.setBounds(80, 17, 225, 40);

		// imdbID
		panel.add(imdbID);
		imdbID.setFont(new Font("Times New Roman", Font.BOLD, 25));
		JLabel lblimdbID;
		lblimdbID = new JLabel("IMdB ID:");
		lblimdbID.setFont(new Font("Cambria", Font.BOLD, 17));
		lblimdbID.setBounds(12, 50, 140, 26);
		panel.add(lblimdbID);
		lblimdbID.setFont(new Font("Times New Roman", Font.PLAIN, 17));
		imdbID.setBounds(164, 50, 140, 26);
		panel.add(plot);

		// plot
		panel.add(plot);
		plot.setFont(new Font("Times New Roman", Font.BOLD, 13));
		JLabel lblPlot;
		lblPlot = new JLabel("Plot:");
		lblPlot.setFont(new Font("Cambria", Font.BOLD, 17));
		lblPlot.setBounds(12, 86, 140, 26);
		panel.add(lblPlot);
		lblPlot.setFont(new Font("Times New Roman", Font.PLAIN, 17));
		plot.setBounds(164, 86, 400, 100);
		panel.add(plot);

		JLabel lblcastsList = new JLabel("Casts:");
		lblcastsList.setFont(new Font("Cambria", Font.BOLD, 17));
		lblcastsList.setBounds(12, 180, 81, 40);
		panel.add(lblcastsList);
		lblcastsList.setSize(new Dimension(350, 300));
		castsList.setModel(new AbstractListModel() {
			String[] values = new String[] {};

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		castsList.setFont(new Font("Times New Roman", Font.PLAIN, 17));
		castsList.setBounds(164, 232, 277, 200);
		panel.add(castsList);
	}
}
