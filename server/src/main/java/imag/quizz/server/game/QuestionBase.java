package imag.quizz.server.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import imag.quizz.common.tool.Log;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Level;

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
            this.correctAnswerIndex = Math.abs((this.question + Question.RANDOM.nextLong()).hashCode()) % 4;
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
            int questions = 0;
            for (final List<Question> themeQuestions : this.themes.values()) {
                questions += themeQuestions.size();
            }
            Log.debug("QuestionBase file parsed. " + this.themes.size() + " themes found with a total of " + questions + " questions");
        }
    }

    public Map<String, List<Question>> getThemes() {
        return this.themes;
    }

    private void parseJsonConfig(final String json) throws IllegalArgumentException {
        try {
            final JsonObject jsonRoot = new JsonParser().parse(json).getAsJsonObject();
            final JsonArray themesArray = jsonRoot.get("themes").getAsJsonArray();
            for (final JsonElement themeObject : themesArray) {
                final JsonObject themeJsonObject = (JsonObject) themeObject;
                final String themeName = themeJsonObject.get("name").getAsString();
                final List<Question> questions = new LinkedList<>();
                final JsonArray questionsArray = themeJsonObject.get("questions").getAsJsonArray();
                for (final JsonElement questionObject : questionsArray) {
                    final JsonObject questionJsonObject = (JsonObject) questionObject;
                    final String question = questionJsonObject.get("question").getAsString();
                    final String answer = questionJsonObject.get("answer").getAsString();
                    final String[] wrongAnswers = new String[3];
                    final JsonArray wrongAnswersArray = questionJsonObject.get("wrongAnswers").getAsJsonArray();
                    int i = 0;
                    for (final JsonElement wrongAnswerObject : wrongAnswersArray) {
                        wrongAnswers[i++] = wrongAnswerObject.getAsString();
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
        } catch (final ClassCastException | NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Malformed configuration file", e);
        }
    }
}
