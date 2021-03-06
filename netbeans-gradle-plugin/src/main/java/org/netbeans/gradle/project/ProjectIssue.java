package org.netbeans.gradle.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jtrim.utils.ExceptionHelper;

public final class ProjectIssue {
    private final List<Entry> entries;

    public ProjectIssue(Collection<Entry> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));

        ExceptionHelper.checkNotNullElements(this.entries, "entries");
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static final class Entry {
        private final Kind kind;
        private final String summary;
        private final String details;

        public Entry(Kind kind, String summary) {
            this(kind, summary, summary);
        }

        public Entry(Kind kind, String summary, String details) {
            ExceptionHelper.checkNotNullArgument(kind, "kind");
            ExceptionHelper.checkNotNullArgument(summary, "summary");
            ExceptionHelper.checkNotNullArgument(details, "details");

            this.kind = kind;
            this.summary = summary;
            this.details = details;
        }

        public Kind getKind() {
            return kind;
        }

        public String getSummary() {
            return summary;
        }

        public String getDetails() {
            return details;
        }
    }

    public enum Kind {
        INFO(1),
        WARNING(2),
        ERROR(3);

        private final int importance;

        private Kind(int importance) {
            this.importance = importance;
        }

        public int getImportance() {
            return importance;
        }
    }
}
