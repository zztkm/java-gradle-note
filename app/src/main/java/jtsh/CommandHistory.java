package jtsh;

import java.util.ArrayList;
import java.util.List;

// 実装途中
public class CommandHistory {
    private List<String> history;

    public CommandHistory() {
        history = new ArrayList<>();
    }

    public boolean insert(String line) {
        return history.add(line);
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }
}