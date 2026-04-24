CREATE DATABASE StudentInformationDB;
USE StudentInformationDB;

-- Part 1: Schema Creation

CREATE TABLE STUDENT (
    StudentID INT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Major VARCHAR(100) NOT NULL,
    AcademicYear VARCHAR(20) NOT NULL
);

CREATE TABLE INSTRUCTOR (
    InstructorID INT PRIMARY KEY,
    InstructorName VARCHAR(100) NOT NULL,
    Department VARCHAR(100) NOT NULL
);

CREATE TABLE COURSE (
    CourseID INT PRIMARY KEY,
    CourseTitle VARCHAR(100) NOT NULL,
    InstructorID INT NOT NULL,
    CONSTRAINT fk_course_instructor
        FOREIGN KEY (InstructorID)
        REFERENCES INSTRUCTOR(InstructorID)
);

CREATE TABLE ENROLLMENT (
    EnrollmentID INT PRIMARY KEY,
    StudentID INT NOT NULL,
    CourseID INT NOT NULL,
    Grade CHAR(2),
    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (StudentID)
        REFERENCES STUDENT(StudentID),
    CONSTRAINT fk_enrollment_course
        FOREIGN KEY (CourseID)
        REFERENCES COURSE(CourseID)
);

-- Verify table creation
SHOW TABLES;
DESCRIBE STUDENT;
DESCRIBE INSTRUCTOR;
DESCRIBE COURSE;
DESCRIBE ENROLLMENT;

-- Part 2: Data Insertion

INSERT INTO STUDENT (StudentID, FirstName, LastName, Major, AcademicYear) VALUES
(1, 'Diego', 'Trevino', 'Computer Science', 'Senior'),
(2, 'Maria', 'Lopez', 'Information Technology', 'Junior'),
(3, 'James', 'Wilson', 'Computer Science', 'Sophomore'),
(4, 'Emily', 'Garcia', 'Cybersecurity', 'Senior'),
(5, 'Carlos', 'Martinez', 'Data Analytics', 'Freshman');

INSERT INTO INSTRUCTOR (InstructorID, InstructorName, Department) VALUES
(101, 'Dr. Smith', 'Computer Science'),
(102, 'Prof. Johnson', 'Information Technology'),
(103, 'Dr. Brown', 'Cybersecurity');

INSERT INTO COURSE (CourseID, CourseTitle, InstructorID) VALUES
(201, 'Database Management Systems', 101),
(202, 'Web Development', 102),
(203, 'Network Security', 103),
(204, 'Data Structures', 101);

INSERT INTO ENROLLMENT (EnrollmentID, StudentID, CourseID, Grade) VALUES
(301, 1, 201, 'A'),
(302, 1, 204, 'B'),
(303, 2, 202, 'A'),
(304, 3, 201, 'B'),
(305, 4, 203, 'A'),
(306, 5, 202, 'C'),
(307, 3, 204, 'A');

-- Verify inserted data
SELECT * FROM STUDENT;
SELECT * FROM INSTRUCTOR;
SELECT * FROM COURSE;
SELECT * FROM ENROLLMENT;

-- Part 3: Update and Delete Operations

-- Update the major of one student
UPDATE STUDENT
SET Major = 'Software Engineering'
WHERE StudentID = 2;

SELECT * FROM STUDENT WHERE StudentID = 2;

-- Change the instructor assigned to one course
UPDATE COURSE
SET InstructorID = 102
WHERE CourseID = 204;

SELECT * FROM COURSE WHERE CourseID = 204;

-- Update the grade of a student in a course
UPDATE ENROLLMENT
SET Grade = 'A'
WHERE StudentID = 5 AND CourseID = 202;

SELECT * FROM ENROLLMENT WHERE StudentID = 5 AND CourseID = 202;

-- Delete one enrollment record
DELETE FROM ENROLLMENT
WHERE EnrollmentID = 302;

SELECT * FROM ENROLLMENT;

-- Attempt to delete a student who still has enrollment records.
-- This should fail because StudentID 1 is still referenced by ENROLLMENT.
DELETE FROM STUDENT
WHERE StudentID = 1;

-- Corrected deletion after resolving the foreign key constraint.
DELETE FROM ENROLLMENT
WHERE StudentID = 1;

DELETE FROM STUDENT
WHERE StudentID = 1;

SELECT * FROM ENROLLMENT;
SELECT * FROM STUDENT;

-- Part 4: Retrieval and JOIN Queries

-- 1. List all students ordered by last name.
SELECT StudentID, FirstName, LastName, Major, AcademicYear
FROM STUDENT
ORDER BY LastName;

-- 2. Display all courses taught by a specific instructor.
SELECT c.CourseID, c.CourseTitle, i.InstructorName
FROM COURSE c
JOIN INSTRUCTOR i ON c.InstructorID = i.InstructorID
WHERE i.InstructorName = 'Prof. Johnson'
ORDER BY c.CourseTitle;

-- 3. Show each student along with the courses they are enrolled in.
SELECT s.FirstName, s.LastName, c.CourseTitle, e.Grade
FROM STUDENT s
JOIN ENROLLMENT e ON s.StudentID = e.StudentID
JOIN COURSE c ON e.CourseID = c.CourseID
ORDER BY s.LastName, c.CourseTitle;

-- 4. Display student names and their grades for a selected course.
SELECT s.FirstName, s.LastName, c.CourseTitle, e.Grade
FROM STUDENT s
JOIN ENROLLMENT e ON s.StudentID = e.StudentID
JOIN COURSE c ON e.CourseID = c.CourseID
WHERE c.CourseTitle = 'Database Management Systems'
ORDER BY s.LastName;

-- 5. Show each course along with the number of students enrolled in it.
SELECT c.CourseID, c.CourseTitle, COUNT(e.StudentID) AS NumberOfStudents
FROM COURSE c
LEFT JOIN ENROLLMENT e ON c.CourseID = e.CourseID
GROUP BY c.CourseID, c.CourseTitle
ORDER BY c.CourseTitle;
