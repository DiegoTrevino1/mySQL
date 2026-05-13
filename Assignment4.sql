DROP DATABASE IF EXISTS TutoringCenterDB;
CREATE DATABASE TutoringCenterDB;
USE TutoringCenterDB;

-- Create tables

CREATE TABLE STUDENT (
    StudentID INT PRIMARY KEY,
    StudentName VARCHAR(100) NOT NULL,
    StudentEmail VARCHAR(100) NOT NULL,
    Major VARCHAR(100) NOT NULL
);

CREATE TABLE TUTOR (
    TutorID INT PRIMARY KEY,
    TutorName VARCHAR(100) NOT NULL,
    TutorEmail VARCHAR(100) NOT NULL
);

CREATE TABLE COURSE (
    CourseID VARCHAR(10) PRIMARY KEY,
    CourseTitle VARCHAR(100) NOT NULL,
    Department VARCHAR(100) NOT NULL
);

CREATE TABLE ROOM (
    RoomID VARCHAR(10) PRIMARY KEY,
    RoomBuilding VARCHAR(100) NOT NULL,
    RoomCapacity INT NOT NULL
);

CREATE TABLE SESSION (
    SessionID INT PRIMARY KEY,
    SessionDate DATE NOT NULL,
    SessionTime TIME NOT NULL,
    StudentID INT NOT NULL,
    TutorID INT NOT NULL,
    CourseID VARCHAR(10) NOT NULL,
    RoomID VARCHAR(10) NOT NULL,
    SessionType VARCHAR(50) NOT NULL,
    HourlyRate DECIMAL(6,2) NOT NULL,
    DurationMinutes INT NOT NULL,
    FOREIGN KEY (StudentID) REFERENCES STUDENT(StudentID),
    FOREIGN KEY (TutorID) REFERENCES TUTOR(TutorID),
    FOREIGN KEY (CourseID) REFERENCES COURSE(CourseID),
    FOREIGN KEY (RoomID) REFERENCES ROOM(RoomID)
);

-- Insert sample data

INSERT INTO STUDENT (StudentID, StudentName, StudentEmail, Major) VALUES
(1001, 'Emma Johnson', 'emma.johnson@university.edu', 'Computer Science'),
(1002, 'Liam Brown', 'liam.brown@university.edu', 'Information Technology'),
(1003, 'Sophia Davis', 'sophia.davis@university.edu', 'Biology'),
(1004, 'Noah Wilson', 'noah.wilson@university.edu', 'Mathematics'),
(1005, 'Olivia Martinez', 'olivia.martinez@university.edu', 'Business Administration');

INSERT INTO TUTOR (TutorID, TutorName, TutorEmail) VALUES
(2001, 'Alice Chen', 'alice.chen@university.edu'),
(2002, 'Bob Martinez', 'bob.martinez@university.edu'),
(2003, 'Carol Smith', 'carol.smith@university.edu'),
(2004, 'David Lee', 'david.lee@university.edu'),
(2005, 'Maya Patel', 'maya.patel@university.edu');

INSERT INTO COURSE (CourseID, CourseTitle, Department) VALUES
('CS101', 'Introduction to Programming', 'Computer Science'),
('CS201', 'Database Systems', 'Computer Science'),
('MATH120', 'College Algebra', 'Mathematics'),
('BIO110', 'General Biology', 'Biology'),
('BUS210', 'Principles of Management', 'Business');

INSERT INTO ROOM (RoomID, RoomBuilding, RoomCapacity) VALUES
('R101', 'Science Hall', 25),
('R102', 'Science Hall', 30),
('R201', 'Library', 12),
('R202', 'Library', 15),
('R301', 'Business Building', 20);

INSERT INTO SESSION (
    SessionID, SessionDate, SessionTime, StudentID, TutorID, CourseID, RoomID,
    SessionType, HourlyRate, DurationMinutes
) VALUES
(1, '2026-05-04', '09:00:00', 1001, 2001, 'CS101', 'R101', 'One-on-One', 20.00, 60),
(2, '2026-05-04', '10:30:00', 1002, 2003, 'CS201', 'R102', 'Group', 18.00, 90),
(3, '2026-05-05', '13:00:00', 1003, 2005, 'BIO110', 'R201', 'One-on-One', 22.00, 60),
(4, '2026-05-06', '14:30:00', 1004, 2004, 'MATH120', 'R202', 'Group', 17.50, 90),
(5, '2026-05-07', '11:00:00', 1005, 2002, 'BUS210', 'R301', 'One-on-One', 19.00, 60);

-- Query 1: Display all tutoring sessions with student name, tutor name, course title, and room ID

SELECT 
    s.SessionID,
    st.StudentName,
    t.TutorName,
    c.CourseTitle,
    s.RoomID,
    s.SessionDate,
    s.SessionTime
FROM SESSION s
JOIN STUDENT st ON s.StudentID = st.StudentID
JOIN TUTOR t ON s.TutorID = t.TutorID
JOIN COURSE c ON s.CourseID = c.CourseID
ORDER BY s.SessionDate, s.SessionTime;

-- Query 2: List all sessions for a specific student

SELECT 
    s.SessionID,
    st.StudentName,
    c.CourseTitle,
    t.TutorName,
    s.SessionDate,
    s.SessionTime
FROM SESSION s
JOIN STUDENT st ON s.StudentID = st.StudentID
JOIN COURSE c ON s.CourseID = c.CourseID
JOIN TUTOR t ON s.TutorID = t.TutorID
WHERE st.StudentName = 'Emma Johnson'
ORDER BY s.SessionDate, s.SessionTime;

-- Query 3: List all sessions conducted by a specific tutor

SELECT 
    s.SessionID,
    t.TutorName,
    st.StudentName,
    c.CourseTitle,
    s.SessionDate,
    s.SessionTime
FROM SESSION s
JOIN TUTOR t ON s.TutorID = t.TutorID
JOIN STUDENT st ON s.StudentID = st.StudentID
JOIN COURSE c ON s.CourseID = c.CourseID
WHERE t.TutorName = 'Alice Chen'
ORDER BY s.SessionDate, s.SessionTime;

-- Query 4: Count the number of sessions per course

SELECT 
    c.CourseID,
    c.CourseTitle,
    COUNT(s.SessionID) AS NumberOfSessions
FROM COURSE c
LEFT JOIN SESSION s ON c.CourseID = s.CourseID
GROUP BY c.CourseID, c.CourseTitle
ORDER BY NumberOfSessions DESC;