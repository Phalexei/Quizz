package imag.quizz.client.ui;
import javax.swing.*;
import java.awt.*;

public class Window {

	private final JButton     topLeftButton;
	private final JButton     topRightButton;
	private final JButton     bottomLeftButton;
	private final JButton     bottomRightButton;
	private final JPanel      buttonsPanel;
	private final JTextPane   questionTextPane;
	private final JPanel      leftPanel;
	private final JTextArea   logsTextArea;
	private final JScrollPane logsScrollPane;
	private final JPanel      rightPanel;
	private final JPanel      mainPanel;
	private final JFrame      frame;

	public Window() {
		this.topLeftButton = new JButton("A");
		this.topRightButton = new JButton("B");
		this.bottomLeftButton = new JButton("C");
		this.bottomRightButton = new JButton("D");

		this.buttonsPanel = new JPanel();
		this.buttonsPanel.setLayout(new GridLayout(2, 2));
		this.buttonsPanel.add(this.topLeftButton);
		this.buttonsPanel.add(this.topRightButton);
		this.buttonsPanel.add(this.bottomLeftButton);
		this.buttonsPanel.add(this.bottomRightButton);

		this.questionTextPane = new JTextPane();
		this.questionTextPane.setEditable(false);
		this.questionTextPane.setText("Question 1"); // TODO

		this.leftPanel = new JPanel();
		this.leftPanel.setLayout(new GridLayout(2, 1));
		this.leftPanel.add(this.questionTextPane);
		this.leftPanel.add(this.buttonsPanel);

		this.logsTextArea = new JTextArea("Logs\nOther Logs");
		this.logsScrollPane = new JScrollPane(this.logsTextArea);
		this.rightPanel = new JPanel();
		this.rightPanel.add(this.logsScrollPane);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new GridLayout(1, 2));
		this.mainPanel.add(this.leftPanel);
		this.mainPanel.add(this.rightPanel);

		this.frame = new JFrame("Quizz");
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setContentPane(this.mainPanel);

		this.frame.pack();
		this.frame.setVisible(true);
		this.frame.setSize(640, 480);
	}
	
	public void setQuestion(final String question) {
		// TODO
	}
	
	public void setAnswer(final int num, final String answer) {
		// TODO
	}
}
