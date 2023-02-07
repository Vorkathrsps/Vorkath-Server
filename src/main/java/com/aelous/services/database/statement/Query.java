package com.aelous.services.database.statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Query {

    public static Query fromSql(String sql) {
        List<String> parameters = new ArrayList<>();

        int length = sql.length();
        StringBuilder builder = new StringBuilder(length);

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inSingleLineComment = false;
        boolean inMultiLineComment = false;

        for (int i = 0; i < length; i++) {
            char c = sql.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else if (inMultiLineComment) {
                if (c == '*' && sql.charAt(i + 1) == '/') {
                    inMultiLineComment = false;
                }
            } else if (inSingleLineComment) {
                if (c == '\n') {
                    inSingleLineComment = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == '/' && sql.charAt(i + 1) == '*') {
                    inMultiLineComment = true;
                } else if (c == '-' && sql.charAt(i + 1) == '-') {
                    inSingleLineComment = true;
                } else if (c == ':' && i + 1 < length && Character.isJavaIdentifierStart(sql.charAt(i + 1))) {
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(sql.charAt(j))) {
                        j++;
                    }
                    String name = sql.substring(i + 1, j);
                    parameters.add(name);
                    c = '?'; // replace the parameter with a question mark
                    i += name.length(); // skip past the end if the parameter
                }
            }
            builder.append(c);
        }

        return new Query(sql, builder.toString(), parameters);
    }

    private String namedSql;
    private final String sql;
    private final List<String> parameters;

    private Query(String namedSql, String sql, List<String> parameters) {
        this.namedSql = namedSql;
        this.sql = sql;
        this.parameters = parameters;
    }

    public String getSql() {
        return sql;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public Collection<Integer> indices(String parameter) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i).equals(parameter)) {
                indices.add(i + 1);
            }
        }
        if (indices.isEmpty()) {
            throw new IllegalArgumentException(String.format("SQL statement doesn't contain the parameter '%s'", parameter));
        }
        return indices;
    }

    public void updateValue(String parameter, String value) {
        namedSql = namedSql.replace(":" + parameter, "'" + value + "'");
    }

    @Override
    public String toString() {
        return namedSql;
    }
}
