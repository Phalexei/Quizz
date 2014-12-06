package imag.quizz.common.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DumbQuestionsGenerator {

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.exit(42);
            return;
        }
        final String fileName = args[0];

        final Map<String, Object> root = new LinkedHashMap<>();
        final List<Object> themesArray = new LinkedList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            final Map<String, Object> themeObject = new LinkedHashMap<>();
            themeObject.put("name", "Theme " + c);
            final List<Object> questionsArray = new LinkedList<>();
            for (int i = 1; i <= 50; i++) {
                final Map<String, Object> questionObject = new LinkedHashMap<>();
                questionObject.put("question", "Question " + c + i + '?');
                questionObject.put("answer", "Answer " + c + i);
                final List<Object> wrongAnswers = new LinkedList<>();
                for (int j = 1; j < 4; j++) {
                    wrongAnswers.add("Wrong Answer " + c + i + " n°" + j);
                }
                questionObject.put("wrongAnswers", wrongAnswers);
                questionsArray.add(questionObject);
            }
            themeObject.put("questions", questionsArray);
            themesArray.add(themeObject);
        }
        root.put("themes", themesArray);

        try (final BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8)) {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(root) + '\n');
        } catch (final IOException e) {
            e.printStackTrace();
        }

        System.out.println("Generated " + args[0] + " with " + ('Z' - 'A' + 1) + " themes and a total of " + (('Z' - 'A' + 1) * 50) + " questions.");
    }
}
