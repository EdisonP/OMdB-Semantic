package main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.EventQueue;

@SuppressWarnings("serial")
public class mainMenu extends JFrame {
	// creating the frame
	private JPanel contentPane;
	public static mainMenu homeFrame = new mainMenu();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					homeFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public mainMenu() {
		// panel
		setIconImage(Toolkit.getDefaultToolkit().getImage("./resources/images/semantic.png"));
		setTitle("Media Semantic Search");
		setResizable(false);
		setPreferredSize(new Dimension(800, 800));
		setMaximumSize(new Dimension(800, 800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(700, 700));
		contentPane.setMaximumSize(new Dimension(400, 400));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		// label for windows title
		JLabel lblWindow = new JLabel("Media Semantic Search");
		lblWindow.setFont(new Font("Sitka Small", Font.BOLD, 36));
		lblWindow.setHorizontalTextPosition(JLabel.CENTER);
		lblWindow.setVerticalTextPosition(JLabel.BOTTOM);
		lblWindow.setIcon(new ImageIcon("./resources/images/logo.png"));
		contentPane.add(lblWindow);

		// button to search people
		JButton btnSearchPeople = new JButton("Search People");
		btnSearchPeople.setFocusTraversalKeysEnabled(false);
		btnSearchPeople.setFocusPainted(false);
		btnSearchPeople.setBackground(SystemColor.controlHighlight);
		btnSearchPeople.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnSearchPeople.setBounds(173, 440, 350, 45);
		btnSearchPeople.setPreferredSize(new Dimension(350, 100));
		btnSearchPeople.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SearchPerson().setVisible(true);
				homeFrame.dispose();
			}
		});
		contentPane.add(btnSearchPeople);

		// button to search media

		JButton btnSearchMedia = new JButton("Search Media");
		btnSearchMedia.setFocusTraversalKeysEnabled(false);
		btnSearchMedia.setFocusPainted(false);
		btnSearchMedia.setBackground(SystemColor.controlHighlight);
		btnSearchMedia.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnSearchMedia.setBounds(173, 440, 350, 45);
		btnSearchMedia.setPreferredSize(new Dimension(350, 100));
		btnSearchMedia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new mediaDetails().setVisible(true);
				homeFrame.dispose();
			}
		});
		contentPane.add(btnSearchMedia);
	}
}
