package imag.quizz.client.ui;
import javax.swing.*;
import javax.swing.text.*;

/**
 * This class is used to deal with the fact that Swing fails at centering
 * text easily on a JTextPane.
 */
public class CenteredTextPaneHandler {

	/**
	 * Creates a JTextPane with the provided text centered horizontally and
	 * vertically.
	 *
	 * @param content the text to put on the JTextPane
	 *
	 * @return a JTextPane with the provided text
	 */
	public static JTextPane create(final String content) {
		final JTextPane result = new JTextPane();
		result.setEditorKit(new CustomEditorKit());
		final SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
		final StyledDocument doc = (StyledDocument) result.getDocument();
		try {
			doc.insertString(0, content, attrs);
		} catch (final BadLocationException ignored) {}
		doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
		return result;
	}

	/**
	 * Sets the text on the provided JTextPane, keeping it centered
	 * horizontally and vertically.
	 *
	 * @param textPane   the JTextPane
	 * @param newContent the new text
	 */
	public static void setText(final JTextPane textPane, final String newContent) {
		final SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
		final StyledDocument doc = (StyledDocument) textPane.getDocument();
		try {
			doc.insertString(0, newContent, attrs);
		} catch (final BadLocationException ignored) {}
		doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
	}

	/**
	 * Magic class to deal with Swing missing features.
	 */
	private static class CustomEditorKit extends StyledEditorKit {

		public ViewFactory getViewFactory() {
			return new StyledViewFactory();
		}

		private static class StyledViewFactory implements ViewFactory {

			public View create(final Element elem) {
				final String kind = elem.getName();
				if (kind != null) {
					switch (kind) {
						case AbstractDocument.ContentElementName:
							return new LabelView(elem);
						case AbstractDocument.ParagraphElementName:
							return new ParagraphView(elem);
						case AbstractDocument.SectionElementName:
							return new CenteredBoxView(elem, View.Y_AXIS);
						case StyleConstants.ComponentElementName:
							return new ComponentView(elem);
						case StyleConstants.IconElementName:
							return new IconView(elem);
					}
				}
				return new LabelView(elem);
			}
		}

		private static class CenteredBoxView extends BoxView {

			public CenteredBoxView(Element elem, int axis) {
				super(elem, axis);
			}

			protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
				super.layoutMajorAxis(targetSpan, axis, offsets, spans);
				final int textBlockHeight;
				if (spans.length > 0) {
					textBlockHeight = spans[spans.length - 1];
				} else {
					textBlockHeight = 0;
				}
				final int offset = (targetSpan - textBlockHeight) / 2;
				for (int i = 0; i < offsets.length; i++) {
					offsets[i] += offset;
				}
			}
		}
	}
}
