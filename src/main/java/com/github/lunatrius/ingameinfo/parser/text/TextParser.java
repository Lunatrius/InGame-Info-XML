package com.github.lunatrius.ingameinfo.parser.text;

import com.github.lunatrius.ingameinfo.Alignment;
import com.github.lunatrius.ingameinfo.parser.IParser;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.value.Value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.lunatrius.ingameinfo.parser.text.Token.TokenType;

public class TextParser implements IParser {
    private final Tokenizer tokenizer;
    private Token token;
    private int level = 0;
    private Alignment alignment = Alignment.TOPLEFT;

    public TextParser() {
        this.tokenizer = new Tokenizer();
    }

    private Token nextToken() {
        this.token = this.tokenizer.nextToken();
        return this.token;
    }

    @Override
    public boolean load(final InputStream inputStream) {
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;StringBuilder content = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            reader.close();
            inputStreamReader.close();

            this.tokenizer.tokenize(content.toString());
        } catch (final Exception e) {
            Reference.logger.fatal("Could not read text configuration file!", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean parse(final Map<Alignment, List<List<Value>>> format) {
        boolean expr;

        try {
            nextToken();
            expr = alignments(format) && this.token.isEof();
        } catch (final Exception e) {
            expr = false;
            Reference.logger.error("Parsing failed at {}!", this.token, e);
        }

        return expr;
    }

    private boolean alignments(final Map<Alignment, List<List<Value>>> format) {
        return alignment(format) && alignmentsTail(format);
    }

    private boolean alignmentsTail(final Map<Alignment, List<List<Value>>> format) {
        if (alignment(format)) {
            alignmentsTail(format);
        }

        return true;
    }

    private boolean alignment(final Map<Alignment, List<List<Value>>> format) {
        boolean expr;
        List<List<Value>> lines = format.get(this.alignment);

        if (lines == null) {
            lines = new ArrayList<>();
        }

        try {
            expr = lines(lines);

            if (expr) {
                format.put(this.alignment, lines);
            }
        } catch (final AlignmentException e) {
            format.put(this.alignment, lines);

            this.alignment = e.getAlignment();
            expr = e.isValid();
        }

        return expr;
    }

    private boolean lines(final List<List<Value>> lines) throws AlignmentException {
        return line(lines) && linesTail(lines);
    }

    private boolean linesTail(final List<List<Value>> lines) throws AlignmentException {
        if (line(lines)) {
            linesTail(lines);
        }

        return true;
    }

    private boolean line(final List<List<Value>> lines) throws AlignmentException {
        final boolean expr;
        final List<Value> values = new ArrayList<>();

        expr = values(values);

        if (this.token.getType().equals(TokenType.NEWLINE)) {
            nextToken();
        }

        if (expr) {
            lines.add(values);
        }

        return expr;
    }

    private boolean values(final List<Value> values) throws AlignmentException {
        return value(values) && valuesTail(values);
    }

    private boolean valuesTail(final List<Value> values) throws AlignmentException {
        if (value(values)) {
            valuesTail(values);
        }

        return true;
    }

    private boolean value(final List<Value> values) throws AlignmentException {
        final boolean expr;

        if (this.token.getType().equals(TokenType.STRING)) {
            expr = string(values, this.token.getLexem());
            nextToken();
        } else if (this.token.getType().equals(TokenType.FUNC_HEAD)) {
            nextToken();
            expr = function(values, this.token.getLexem());
        } else if (this.level == 0 && TokenType.EXCEPTIONS.contains(this.token.getType())) {
            expr = string(values, this.token.getLexem());
            nextToken();
        } else {
            expr = false;
        }

        return expr;
    }

    private boolean string(final List<Value> values, final String lexem) {
        final Value value = Value.fromString("str").setRawValue(lexem, true);
        values.add(value);
        return true;
    }

    private boolean function(final List<Value> values, final String lexem) throws AlignmentException {
        boolean expr;

        this.level++;

        Value value = Value.fromString(lexem);
        if (!value.isValid()) {
            value = Value.fromString("var");
            value.setRawValue(lexem, true);
        } else {
            value.setRawValue("", true);
        }

        if (this.token.getType().equals(TokenType.STRING)) {
            nextToken();

            expr = argumentGroupA(value);

            if (this.token.getType().equals(TokenType.FUNC_TAIL)) {
                nextToken();
            } else {
                expr = false;
            }
        } else {
            expr = false;
        }

        this.level--;

        final Alignment alignment = Alignment.parse(lexem);
        if (alignment != null) {
            throw new AlignmentException(alignment, expr);
        } else if (expr) {
            values.add(value);
        }

        return expr;
    }

    private boolean argumentGroupA(final Value value) throws AlignmentException {
        boolean expr;

        if (this.token.getType().equals(TokenType.ARGS_HEAD)) {
            nextToken();

            expr = argumentsA(value);

            expr &= argumentGroupB(value);

            if (this.token.getType().equals(TokenType.ARGS_TAIL)) {
                nextToken();
            } else {
                expr = false;
            }
        } else {
            expr = true;
        }

        return expr;
    }

    private boolean argumentsA(final Value value) throws AlignmentException {
        return argument(value) && argumentsATail(value);
    }

    private boolean argumentsATail(final Value value) throws AlignmentException {
        if (this.token.getType().equals(TokenType.ARGS_SEPARATOR)) {
            nextToken();

            if (argument(value)) {
                argumentsATail(value);
            }
        }

        return true;
    }

    private boolean argument(final Value value) throws AlignmentException {
        final boolean expr;

        if (this.token.getType().equals(TokenType.STRING)) {
            expr = string(value.values, this.token.getLexem());
            nextToken();
        } else if (this.token.getType().equals(TokenType.FUNC_HEAD)) {
            nextToken();
            expr = function(value.values, this.token.getLexem());
        } else {
            expr = string(value.values, "");
        }

        return expr;
    }

    private boolean argumentGroupB(final Value value) throws AlignmentException {
        boolean expr;

        if (this.token.getType().equals(TokenType.ARGS_HEAD)) {
            nextToken();

            expr = argumentsB(value);

            if (this.token.getType().equals(TokenType.ARGS_TAIL)) {
                nextToken();
            } else {
                expr = false;
            }
        } else {
            expr = true;
        }

        return expr;
    }

    private boolean argumentsB(final Value value) throws AlignmentException {
        return argument(value) && argumentsBTail(value);
    }

    private boolean argumentsBTail(final Value value) throws AlignmentException {
        if (this.token.getType().equals(TokenType.ARGS_SEPARATOR)) {
            nextToken();

            if (argument(value)) {
                argumentsBTail(value);
            }
        }

        return true;
    }
}
