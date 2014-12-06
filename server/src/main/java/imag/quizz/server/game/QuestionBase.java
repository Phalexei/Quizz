package imag.quizz.server.game;

import imag.quizz.common.tool.Log;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class QuestionBase {

    private static final URL questionBaseUrl = QuestionBase.class.getResource("/questionBase.json");

    public static final class Question {

        private static final Random RANDOM = new Random();

        private final String   question;
        private final String[] answers;
        private final int      correctAnswerIndex;

        public Question(final String question, final String answer, final String[] wrongAnswers) {
            Validate.isTrue(wrongAnswers.length == 3);
            this.question = question;

            // Create an array with the 4 answers
            this.correctAnswerIndex = (this.question + Question.RANDOM.nextLong()).hashCode() % 4;
            this.answers = new String[4];
            int j = 0;
            for (int i = 0; i < 4; i++) {
                this.answers[i] = this.correctAnswerIndex == i ? answer : wrongAnswers[j++];
            }
        }

        public String getQuestion() {
            return this.question;
        }

        public String[] getAnswers() {
            return this.answers;
        }

        public int getCorrectAnswerIndex() {
            return this.correctAnswerIndex;
        }
    }

    private final Map<String, List<Question>> themes;

    public QuestionBase() throws IOException {
        this.themes = new HashMap<>();
        try {
            final Path filePath = Paths.get(QuestionBase.questionBaseUrl.toURI());
            final String configString = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            this.parseJsonConfig(configString);
        } catch (final IOException | URISyntaxException | IllegalArgumentException e1) {
            throw new IOException("Error while reading configuration file", e1);
        }
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("QuestionBase file parsed. " + this.themes.size() + " themes found");
        }
    }

    public Map<String, List<Question>> getThemes() {
        return this.themes;
    }

    private void parseJsonConfig(final String json) throws IllegalArgumentException {
        try {
            final JSONObject jsonRoot = (JSONObject) new JSONParser().parse(json);
            final JSONArray themesArray = (JSONArray) jsonRoot.get("themes");
            for (final Object themeObject : themesArray) {
                final JSONObject themeJsonObject = (JSONObject) themeObject;
                final String themeName = (String) themeJsonObject.get("name");
                final List<Question> questions = new LinkedList<>();
                final JSONArray questionsArray = (JSONArray) themeJsonObject.get("questions");
                for (final Object questionObject : questionsArray) {
                    final JSONObject questionJsonObject = (JSONObject) questionObject;
                    final String question = (String) questionJsonObject.get("question");
                    final String answer = (String) questionJsonObject.get("answer");
                    final String[] wrongAnswers = new String[3];
                    final JSONArray wrongAnswersArray = (JSONArray) questionJsonObject.get("wrongAnswers");
                    int i = 0;
                    for (final Object wrongAnswerObject : wrongAnswersArray) {
                        wrongAnswers[i++] = (String) wrongAnswerObject;
                    }
                    questions.add(new Question(question, answer, wrongAnswers));
                }
                if (questions.size() < 5) {
                    throw new IllegalArgumentException("Every theme should have AT THE VERY LEAST 5 questions (10 or more is better)");
                }
                this.themes.put(themeName, questions);
            }
            if (this.themes.size() < 8) {
                throw new IllegalArgumentException("There should be AT THE VERY LEAST 8 themes (more is better)");
            }
        } catch (final ParseException | ClassCastException | NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Malformed configuration file", e);
        }
    }
}
