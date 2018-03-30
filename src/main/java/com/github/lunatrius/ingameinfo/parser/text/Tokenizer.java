package com.github.lunatrius.ingameinfo.parser.text;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.regex.Matcher;

import static com.github.lunatrius.ingameinfo.parser.text.Token.TokenType;

public class Tokenizer {
    private final Queue<Token> tokens;

    public Tokenizer() {
        this.tokens = new LinkedList<>();
    }

    public void tokenize(String str) throws Exception {
        Location start = new Location(0, 0);
        final Location end = new Location(0, 0);
        boolean match;

        this.tokens.clear();

        str = str.trim();

        while (str.length() > 0) {
            match = false;
            for (final TokenType tokenType : TokenType.values()) {
                final Matcher matcher = tokenType.getPattern().matcher(str);
                if (matcher.find()) {
                    match = true;
                    final String lexem = matcher.group();
                    str = matcher.replaceFirst("");

                    if (tokenType.equals(TokenType.NEWLINE)) {
                        final int lines = lexem.length() - lexem.replace("\n", "").length();
                        end.setRow(start.getRow() + lines);
                        end.setColumn(0);
                    } else {
                        end.setColumn(start.getColumn() + lexem.length());
                    }

                    this.tokens.add(new Token(lexem, start.clone(), end.clone(), tokenType));
                    start = end.clone();
                    break;
                }
            }

            if (!match) {
                throw new InvalidTokenException(String.format(Locale.ENGLISH, "Invalid token at %s!", start));
            }
        }

        this.tokens.add(new Token("", start.clone(), end.clone(), TokenType.EOF));
    }

    public Token nextToken() {
        return this.tokens.remove();
    }
}
