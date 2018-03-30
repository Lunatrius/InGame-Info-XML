package com.github.lunatrius.ingameinfo.parser.text;

import com.github.lunatrius.ingameinfo.Alignment;

public class AlignmentException extends Exception {
    private final Alignment alignment;
    private final boolean valid;

    public AlignmentException(final Alignment alignment, final boolean valid) {
        this.alignment = alignment;
        this.valid = valid;
    }

    public Alignment getAlignment() {
        return this.alignment;
    }

    public boolean isValid() {
        return this.valid;
    }
}
