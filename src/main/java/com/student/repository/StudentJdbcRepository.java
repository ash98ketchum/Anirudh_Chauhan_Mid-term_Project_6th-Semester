package com.student.repository;

import com.student.model.Student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * JDBC IMPLEMENTATION OF STUDENT REPOSITORY
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * @Repository is a SPECIALIZATION of @Component. It does three things:
 *
 * 1. COMPONENT SCANNING: Tells Spring to auto-detect this class and create
 *    a singleton bean (one shared instance) managed by the Spring container.
 *
 * 2. EXCEPTION TRANSLATION: Spring automatically translates low-level JDBC
 *    exceptions (like java.sql.SQLException) into Spring's DataAccessException
 *    hierarchy. This means your service layer doesn't need to know about
 *    JDBC-specific exceptions — it works with Spring's clean exception types.
 *    Example: SQLException → DuplicateKeyException, DataIntegrityViolationException
 *
 * 3. SEMANTIC CLARITY: Any developer reading the code immediately knows
 *    "this class talks to the database." It's self-documenting.
 *
 * WHERE ELSE IS @Repository USED?
 *   • Spring Data JPA: Repository interfaces extend JpaRepository
 *   • Spring Data MongoDB: MongoRepository
 *   • Any class that performs data access operations
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * WHAT IS JdbcTemplate?
 * ─────────────────────────────────────────────────────────────────────────────
 * JdbcTemplate is Spring's utility class that simplifies JDBC operations.
 * It handles ALL the boilerplate code that raw JDBC requires:
 *
 *   RAW JDBC (what you'd write without JdbcTemplate):
 *   ─────────────────────────────────────────────────
 *   Connection conn = null;
 *   PreparedStatement ps = null;
 *   ResultSet rs = null;
 *   try {
 *       conn = dataSource.getConnection();          // 1. Get connection
 *       ps = conn.prepareStatement("SELECT ...");   // 2. Create statement
 *       ps.setInt(1, id);                           // 3. Set parameters
 *       rs = ps.executeQuery();                     // 4. Execute query
 *       if (rs.next()) {                            // 5. Process results
 *           student.setName(rs.getString("name"));
 *       }
 *   } catch (SQLException e) {                     // 6. Handle exceptions
 *       throw new RuntimeException(e);
 *   } finally {
 *       if (rs != null) rs.close();                 // 7. Close ResultSet
 *       if (ps != null) ps.close();                 // 8. Close Statement
 *       if (conn != null) conn.close();             // 9. Close Connection
 *   }
 *
 *   WITH JdbcTemplate (same thing in ONE line):
 *   ────────────────────────────────────────────
 *   Student student = jdbcTemplate.queryForObject(
 *       "SELECT * FROM students WHERE id = ?",
 *       rowMapper, id
 *   );
 *
 * JdbcTemplate internally:
 *   1. Borrows a connection from the HikariCP connection pool
 *   2. Creates a PreparedStatement (safe from SQL injection)
 *   3. Sets parameters (the '?' placeholders)
 *   4. Executes the query
 *   5. Maps the ResultSet to Java objects using the RowMapper
 *   6. Closes everything (ResultSet, Statement, Connection) — even if exceptions occur
 *   7. Returns the connection to the pool (NOT closes it — reuses it!)
 *
 * Analogy: JdbcTemplate is like a personal assistant for database operations.
 *          You just say "get me student #5" and it handles all the tedious
 *          paperwork (connections, statements, error handling, cleanup).
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@Repository
public class StudentJdbcRepository implements StudentRepository {

    // ─────────────────────────────────────────────────────────────────────────
    // LOGGER
    // ─────────────────────────────────────────────────────────────────────────
    // SLF4J (Simple Logging Facade for Java) provides a unified logging API.
    // LoggerFactory creates a logger specific to THIS class — log messages
    // will show "StudentJdbcRepository" as the source, making debugging easier.
    //
    // WHY NOT System.out.println()?
    //   • No log levels (can't filter INFO vs DEBUG vs ERROR)
    //   • No timestamps, thread info, or class names
    //   • Can't redirect to files, monitoring systems, or log aggregators
    //   • Can't be turned off in production
    //   • Professional applications ALWAYS use a logging framework
    // ─────────────────────────────────────────────────────────────────────────
    private static final Logger log = LoggerFactory.getLogger(StudentJdbcRepository.class);

    // ─────────────────────────────────────────────────────────────────────────
    // DEPENDENCY: JdbcTemplate
    // ─────────────────────────────────────────────────────────────────────────
    // 'final' means this field can only be set ONCE (in the constructor).
    // This guarantees the JdbcTemplate is never accidentally replaced or set
    // to null after construction — a form of immutability.
    // ─────────────────────────────────────────────────────────────────────────
    private final JdbcTemplate jdbcTemplate;

    // ─────────────────────────────────────────────────────────────────────────
    // ROW MAPPER
    // ─────────────────────────────────────────────────────────────────────────
    // A RowMapper<Student> is a function that converts ONE ROW of a ResultSet
    // into a Student Java object. It's called once for each row.
    //
    // WHAT IS A ResultSet?
    //   When you execute a SQL query, the database returns results as a
    //   ResultSet — essentially a table of rows. The RowMapper reads each
    //   row and creates a Student object from the column values.
    //
    // HOW IT WORKS:
    //   ResultSet row: | id=1 | name="John" | email="john@x.com" | course="CS" |
    //                            ↓ RowMapper transforms ↓
    //   Student object: Student{id=1, name="John", email="john@x.com", course="CS"}
    //
    // WHY A SEPARATE RowMapper?
    //   • DRY (Don't Repeat Yourself): We reuse this mapper in findAll(),
    //     findById(), etc. Without it, we'd duplicate the mapping code everywhere.
    //   • Single Responsibility: The mapper ONLY knows how to convert rows to
    //     objects. If the table schema changes, we fix it in ONE place.
    //
    // LAMBDA SYNTAX:
    //   (rs, rowNum) → { ... }
    //   • rs      → The ResultSet positioned at the current row
    //   • rowNum  → The index of the current row (0-based), useful for debugging
    //   • The lambda body reads column values and creates a Student
    // ─────────────────────────────────────────────────────────────────────────
    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> {
        Student student = new Student();
        student.setId(rs.getInt("id"));             // Read the 'id' column as an integer
        student.setName(rs.getString("name"));       // Read the 'name' column as a string
        student.setEmail(rs.getString("email"));     // Read the 'email' column as a string
        student.setCourse(rs.getString("course"));   // Read the 'course' column as a string
        return student;
    };

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR INJECTION
    // ═══════════════════════════════════════════════════════════════════════════
    // Spring injects the JdbcTemplate bean automatically.
    //
    // WHY CONSTRUCTOR INJECTION (not @Autowired on field)?
    //   1. IMMUTABILITY: Fields can be 'final' — guaranteed to be set once.
    //   2. TESTABILITY: In unit tests, you can pass a mock JdbcTemplate
    //      directly via the constructor — no need for Spring to run.
    //   3. EXPLICIT DEPENDENCIES: All required dependencies are visible in
    //      the constructor signature. No "hidden" dependencies.
    //   4. FAIL-FAST: If JdbcTemplate is not available, the app fails at
    //      startup (constructor time), not at runtime when a method is called.
    //
    // NOTE: When a class has ONLY ONE constructor, Spring automatically uses
    //       it for dependency injection — the @Autowired annotation is optional.
    // ═══════════════════════════════════════════════════════════════════════════
    public StudentJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SAVE — INSERT a new student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Inserts a new student into the database and returns the student with
     * the auto-generated id.
     *
     * SQL: INSERT INTO students (name, email, course) VALUES (?, ?, ?)
     *
     * WHY '?' PLACEHOLDERS (PreparedStatement)?
     * ──────────────────────────────────────────
     * NEVER build SQL with string concatenation:
     *   ❌ "INSERT INTO students (name) VALUES ('" + name + "')"
     *   If name = "'; DROP TABLE students; --", you just lost your data.
     *   This is a SQL INJECTION attack — the #1 web vulnerability (OWASP Top 10).
     *
     * PreparedStatement separates SQL STRUCTURE from DATA:
     *   ✅ "INSERT INTO students (name) VALUES (?)" + parameter binding
     *   The '?' is a parameter placeholder. The database treats it as DATA,
     *   never as SQL code. Even if the user types malicious SQL, it's treated
     *   as a harmless string value.
     *
     * Analogy: It's like a fill-in-the-blank form. The form structure is fixed:
     *          "My name is ___." Whatever you write in the blank, it's always
     *          just a name — it can't change the sentence structure.
     *
     * WHY KeyHolder?
     * ──────────────
     * When the database auto-generates the id (SERIAL), we need to retrieve
     * it. KeyHolder captures the generated key after the INSERT executes.
     * Without it, we wouldn't know the id of the newly created student.
     */
    @Override
    public Student save(Student student) {
        log.debug("Saving new student: {}", student);

        String sql = "INSERT INTO students (name, email, course) VALUES (?, ?, ?)";

        // KeyHolder captures auto-generated keys (like our SERIAL id)
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // jdbcTemplate.update() with a PreparedStatementCreator gives us full
        // control: we can specify which columns to return (the generated id).
        jdbcTemplate.update(connection -> {
            // connection.prepareStatement() creates a PreparedStatement.
            // Statement.RETURN_GENERATED_KEYS tells the driver to return the
            // auto-generated id after the INSERT.
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getName());    // 1st '?' → name
            ps.setString(2, student.getEmail());   // 2nd '?' → email
            ps.setString(3, student.getCourse());  // 3rd '?' → course
            return ps;
        }, keyHolder);

        // Extract the generated id from the KeyHolder and set it on the student
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            student.setId(generatedId.intValue());
        }

        log.info("Successfully saved student with id: {}", student.getId());
        return student;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FIND ALL — SELECT all students
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Retrieves all students from the database.
     *
     * SQL: SELECT * FROM students ORDER BY id ASC
     *
     * jdbcTemplate.query() executes the SQL and uses our RowMapper to convert
     * EACH row of the ResultSet into a Student object, collecting them into a List.
     *
     * Internally, JdbcTemplate does:
     *   1. Get connection from pool
     *   2. Create PreparedStatement with the SQL
     *   3. Execute query → get ResultSet
     *   4. For each row in ResultSet: call studentRowMapper.mapRow(rs, rowNum)
     *   5. Collect all Student objects into a List
     *   6. Close ResultSet, Statement, return Connection to pool
     *   7. Return the List
     *
     * ORDER BY id ASC → Ensures consistent ordering. Without this, the database
     * can return rows in any order it wants (usually insertion order, but not
     * guaranteed). Explicit ordering prevents surprises.
     */
    @Override
    public List<Student> findAll() {
        log.debug("Fetching all students");
        String sql = "SELECT * FROM students ORDER BY id ASC";
        List<Student> students = jdbcTemplate.query(sql, studentRowMapper);
        log.debug("Found {} students", students.size());
        return students;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FIND BY ID — SELECT a single student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Finds a single student by their primary key.
     *
     * SQL: SELECT * FROM students WHERE id = ?
     *
     * queryForObject() expects EXACTLY ONE row. If:
     *   • 1 row found → Returns the mapped Student object
     *   • 0 rows found → Throws EmptyResultDataAccessException
     *   • 2+ rows found → Throws IncorrectResultSizeDataAccessException
     *
     * We catch EmptyResultDataAccessException and return Optional.empty()
     * instead, because "student not found" is a NORMAL business case, not
     * an exceptional error. The caller can then decide what to do:
     *   repository.findById(99).orElseThrow(() -> new StudentNotFoundException(99));
     */
    @Override
    public Optional<Student> findById(int id) {
        log.debug("Fetching student with id: {}", id);
        String sql = "SELECT * FROM students WHERE id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, studentRowMapper, id);
            log.debug("Found student: {}", student);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            // This is expected when the id doesn't exist — not a bug.
            log.debug("No student found with id: {}", id);
            return Optional.empty();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UPDATE — Modify an existing student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Updates all fields of an existing student.
     *
     * SQL: UPDATE students SET name = ?, email = ?, course = ? WHERE id = ?
     *
     * jdbcTemplate.update() returns the number of rows affected:
     *   • 1 → Success (one student updated)
     *   • 0 → No student with that id exists
     *
     * We set the id on the student object and return it, giving the caller
     * the complete updated student.
     *
     * WHY UPDATE ALL FIELDS?
     * In a real app, you might want PATCH semantics (update only changed fields).
     * That requires more complex SQL generation. For simplicity and clarity,
     * we use PUT semantics: send ALL fields, replace ALL fields.
     */
    @Override
    public Student update(int id, Student student) {
        log.debug("Updating student with id: {} → {}", id, student);
        String sql = "UPDATE students SET name = ?, email = ?, course = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                student.getName(),    // 1st '?' → new name
                student.getEmail(),   // 2nd '?' → new email
                student.getCourse(),  // 3rd '?' → new course
                id                    // 4th '?' → WHERE id = ?
        );
        student.setId(id);
        log.info("Successfully updated student with id: {}", id);
        return student;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DELETE — Remove a student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Deletes a student by their id.
     *
     * SQL: DELETE FROM students WHERE id = ?
     *
     * Returns true if a row was actually deleted (rowsAffected > 0),
     * false if no student with that id existed.
     */
    @Override
    public boolean delete(int id) {
        log.debug("Deleting student with id: {}", id);
        String sql = "DELETE FROM students WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        boolean deleted = rowsAffected > 0;
        if (deleted) {
            log.info("Successfully deleted student with id: {}", id);
        } else {
            log.debug("No student found to delete with id: {}", id);
        }
        return deleted;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EXISTS BY EMAIL — Check for duplicate emails
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * Checks if any student has the given email.
     *
     * SQL: SELECT COUNT(*) FROM students WHERE email = ?
     *
     * queryForObject() with Integer.class → JdbcTemplate automatically extracts
     * the single integer result from the ResultSet.
     *
     * WHY CHECK IN THE APP WHEN THE DB HAS A UNIQUE CONSTRAINT?
     *   • Better user experience: We return a clear "email already exists" message
     *     instead of a cryptic database error.
     *   • Defense in depth: Even if the app check has a race condition (two
     *     simultaneous requests), the database UNIQUE constraint is the final
     *     safety net.
     */
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * Checks if any OTHER student (excluding the given id) has the given email.
     * Used during updates so a student can keep their own email.
     *
     * SQL: SELECT COUNT(*) FROM students WHERE email = ? AND id != ?
     */
    @Override
    public boolean existsByEmailAndIdNot(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, excludeId);
        return count != null && count > 0;
    }
}
