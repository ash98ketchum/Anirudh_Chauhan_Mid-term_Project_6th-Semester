-- ═══════════════════════════════════════════════════════════════════════════════
-- DATABASE SCHEMA FOR STUDENT CRUD APPLICATION
-- ═══════════════════════════════════════════════════════════════════════════════
-- This file is automatically executed by Spring Boot on startup when
-- spring.sql.init.mode=always is set in application.properties.
--
-- NOTE: Run the following command MANUALLY in psql or pgAdmin FIRST to create
-- the database (Spring Boot cannot create databases, only tables within them):
--
--   CREATE DATABASE student_db;
--
-- ═══════════════════════════════════════════════════════════════════════════════

-- ──────────────────────────────────────────────────────────────────────────────
-- TABLE: students
-- ──────────────────────────────────────────────────────────────────────────────
-- IF NOT EXISTS → Prevents errors on repeated app startups. Without this,
--   running the app a second time would throw "table already exists" error.
--   This makes the script IDEMPOTENT (safe to run multiple times).
--
-- COLUMN DETAILS:
-- ──────────────────────────────────────────────────────────────────────────────
--
-- id SERIAL PRIMARY KEY
--   • SERIAL    → PostgreSQL-specific shorthand for an auto-incrementing integer.
--                 Behind the scenes, it creates a SEQUENCE and sets the column's
--                 default to nextval('students_id_seq').
--                 Equivalent to: INTEGER NOT NULL DEFAULT nextval('students_id_seq')
--   • PRIMARY KEY → Uniquely identifies each row. Automatically adds:
--                   - NOT NULL constraint (cannot be empty)
--                   - UNIQUE constraint (no duplicates)
--                   - Creates a B-tree index for fast lookups
--   • Why INTEGER? → Student IDs don't need decimal points. Integers are compact
--                    (4 bytes), fast to compare, and perfect for surrogate keys.
--
-- name VARCHAR(100) NOT NULL
--   • VARCHAR(100) → Variable-length string, up to 100 characters.
--                    Unlike CHAR(100) which always uses 100 bytes, VARCHAR only
--                    uses as much space as the actual string needs + 1 byte overhead.
--   • NOT NULL      → Every student MUST have a name. The database will REJECT
--                     any INSERT that doesn't include a name.
--   • Why 100?      → Accommodates most full names worldwide while preventing
--                     abuse (someone inserting a 10,000-character "name").
--
-- email VARCHAR(150) NOT NULL UNIQUE
--   • VARCHAR(150) → Emails can be longer than names (user@subdomain.domain.tld).
--                    RFC 5321 allows up to 254 chars, but 150 covers 99.9% of cases.
--   • NOT NULL      → Email is required (often used for communication/login).
--   • UNIQUE        → No two students can have the same email. PostgreSQL
--                     automatically creates a B-tree index on this column for
--                     fast duplicate checking. This enforces data integrity at
--                     the DATABASE level (even if the application has a bug).
--
-- course VARCHAR(100) NOT NULL
--   • VARCHAR(100) → Course names like "Computer Science", "Mechanical Engineering".
--   • NOT NULL      → Every student must be enrolled in a course.
--
-- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--   • TIMESTAMP     → Stores date + time (e.g., 2026-04-29 14:30:00).
--   • DEFAULT CURRENT_TIMESTAMP → If no value is provided during INSERT,
--                     PostgreSQL automatically fills in the current date/time.
--                     Useful for auditing ("when was this record created?").
--   • Why not NOT NULL? → The DEFAULT handles it, so NULL is never actually stored.
--
-- ──────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS students (
    id         SERIAL       PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    course     VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ──────────────────────────────────────────────────────────────────────────────
-- INDEX: idx_students_email
-- ──────────────────────────────────────────────────────────────────────────────
-- Although UNIQUE already creates an index, this is here for documentation.
-- In a real app, you might add indexes on columns you frequently search/filter by.
-- Example: If you often query students by course:
--
--   CREATE INDEX IF NOT EXISTS idx_students_course ON students(course);
--
-- Think of an index like a book's table of contents — instead of reading
-- every page (full table scan), you jump directly to the right chapter.
-- ──────────────────────────────────────────────────────────────────────────────
