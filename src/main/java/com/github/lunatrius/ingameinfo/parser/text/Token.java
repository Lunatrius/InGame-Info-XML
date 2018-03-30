package com.github.lunatrius.ingameinfo.parser.text;

import java.util.EnumSet;
import java.util.Locale;
import java.util.regex.Pattern;

public class Token {
    public enum TokenType {
        EOF("$"),
        FUNC_HEAD("<"),
        FUNC_TAIL(">"),
        ARGS_HEAD("\\["),
        ARGS_SEPARATOR("/"),
        ARGS_TAIL("\\]"),
        NEWLINE("\\n+"),
        STRING("(\\\\[<>\\[/\\]\\\\]|[^<>\\[/\\]\\n])+");

        public final static EnumSet<TokenType> EXCEPTIONS = EnumSet.of(FUNC_TAIL, ARGS_HEAD, ARGS_SEPARATOR, ARGS_TAIL);
        private final Pattern pattern;

        TokenType(final String regex) {
            this.pattern = Pattern.compile("^" + regex);
        }

        public Pattern getPattern() {
            return this.pattern;
        }
    }

    private final String lexem;
    private final Location locationStart;
    private final Location locationEnd;
    private final TokenType type;

    public Token(final String lexem, final Location start, final Location end, final TokenType type) {
        this.lexem = lexem;
        this.locationStart = start;
        this.locationEnd = end;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s, %s, %s, %s, %s", this.lexem.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"), this.type, this.locationStart, this.locationEnd, this.isEof());
    }

    public String getLexem() {
        return this.lexem;
    }

    public TokenType getType() {
        return this.type;
    }

    public boolean isEof() {
        return this.type.equals(TokenType.EOF);
    }
}
