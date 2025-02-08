package is.murmur.Model.DAO;

public class QueryBuilder {
    private final StringBuilder query;
    private QueryState state;

    // Enum che rappresenta gli stati possibili durante la costruzione della query
    private enum QueryState {
        START,      // Stato iniziale, nessuna parte della query ancora aggiunta
        SELECT,     // Dopo aver chiamato select()
        FROM,       // Dopo aver chiamato from() (o dopo una JOIN)
        JOIN,       // Dopo aver aggiunto una JOIN (INNER, LEFT, RIGHT)
        WHERE,      // Dopo aver aggiunto where()
        INSERT,     // Dopo aver chiamato insertInto()
        VALUES,     // Dopo aver chiamato values() (completa la query INSERT)
        UPDATE,     // Dopo aver chiamato update()
        SET,        // Dopo aver chiamato set() (completa la query UPDATE)
        DELETE      // Dopo aver chiamato deleteFrom()
    }

    public QueryBuilder() {
        this.query = new StringBuilder();
        this.state = QueryState.START;
    }

    // Metodo per costruire una SELECT
    public QueryBuilder select(String columns) {
        if (state != QueryState.START) {
            throw new IllegalStateException("SELECT può essere usato solo all'inizio della query.");
        }
        query.append("SELECT ").append(columns).append(" ");
        state = QueryState.SELECT;
        return this;
    }

    // Metodo per specificare la clausola FROM
    public QueryBuilder from(String table) {
        if (state != QueryState.SELECT && state != QueryState.JOIN) {
            throw new IllegalStateException("FROM può seguire solo SELECT o una JOIN.");
        }
        query.append("FROM ").append(table).append(" ");
        state = QueryState.FROM;
        return this;
    }

    // Metodo per aggiungere la clausola WHERE
    public QueryBuilder where(String condition) {
        if (state != QueryState.FROM && state != QueryState.JOIN && state != QueryState.SET) {
            throw new IllegalStateException("WHERE può seguire FROM, JOIN o SET.");
        }
        query.append("WHERE ").append(condition).append(" ");
        state = QueryState.WHERE;
        return this;
    }

    // Metodo per costruire una INSERT
    public QueryBuilder insertInto(String table, String columns) {
        if (state != QueryState.START) {
            throw new IllegalStateException("INSERT INTO deve essere la prima clausola della query.");
        }
        query.append("INSERT INTO ").append(table)
                .append(" (").append(columns).append(") ");
        state = QueryState.INSERT;
        return this;
    }

    // Metodo per specificare la clausola VALUES dopo INSERT INTO
    public QueryBuilder values(String placeholders) {
        if (state != QueryState.INSERT) {
            throw new IllegalStateException("VALUES deve seguire INSERT INTO.");
        }
        query.append("VALUES (").append(placeholders).append(") ");
        state = QueryState.VALUES;
        return this;
    }

    // Metodo per aggiungere una INNER JOIN
    public QueryBuilder innerJoin(String table, String condition) {
        if (state != QueryState.FROM && state != QueryState.JOIN) {
            throw new IllegalStateException("INNER JOIN può seguire solo FROM o una JOIN.");
        }
        query.append("INNER JOIN ").append(table)
                .append(" ON ").append(condition).append(" ");
        state = QueryState.JOIN;
        return this;
    }

    // Metodo per aggiungere una LEFT JOIN
    public QueryBuilder leftJoin(String table, String condition) {
        if (state != QueryState.FROM && state != QueryState.JOIN) {
            throw new IllegalStateException("LEFT JOIN può seguire solo FROM o una JOIN.");
        }
        query.append("LEFT JOIN ").append(table)
                .append(" ON ").append(condition).append(" ");
        state = QueryState.JOIN;
        return this;
    }

    // Metodo per aggiungere una RIGHT JOIN
    public QueryBuilder rightJoin(String table, String condition) {
        if (state != QueryState.FROM && state != QueryState.JOIN) {
            throw new IllegalStateException("RIGHT JOIN può seguire solo FROM o una JOIN.");
        }
        query.append("RIGHT JOIN ").append(table)
                .append(" ON ").append(condition).append(" ");
        state = QueryState.JOIN;
        return this;
    }

    // Metodo per costruire una UPDATE
    public QueryBuilder update(String table) {
        if (state != QueryState.START) {
            throw new IllegalStateException("UPDATE deve essere la prima clausola della query.");
        }
        query.append("UPDATE ").append(table).append(" ");
        state = QueryState.UPDATE;
        return this;
    }

    // Metodo per impostare i nuovi valori in una UPDATE
    public QueryBuilder set(String assignments) {
        if (state != QueryState.UPDATE) {
            throw new IllegalStateException("SET deve seguire UPDATE.");
        }
        query.append("SET ").append(assignments).append(" ");
        state = QueryState.SET;
        return this;
    }

    // Metodo per costruire una DELETE
    public QueryBuilder deleteFrom(String table) {
        if (state != QueryState.START) {
            throw new IllegalStateException("DELETE FROM deve essere la prima clausola della query.");
        }
        query.append("DELETE FROM ").append(table).append(" ");
        state = QueryState.DELETE;
        return this;
    }

    // Metodo build() che restituisce la query finale e ne verifica la completezza
    public String build() {
        switch (state) {
            case START:
                throw new IllegalStateException("Nessuna query è stata costruita.");
            case SELECT:
                throw new IllegalStateException("Query SELECT incompleta: manca la clausola FROM.");
            case INSERT:
                throw new IllegalStateException("Query INSERT incompleta: manca la clausola VALUES.");
            case UPDATE:
                throw new IllegalStateException("Query UPDATE incompleta: manca la clausola SET.");
            default:
                // Se lo stato è WHERE, FROM, JOIN, VALUES, SET o DELETE consideriamo la query completa
                return query.toString().trim();
        }
    }
}