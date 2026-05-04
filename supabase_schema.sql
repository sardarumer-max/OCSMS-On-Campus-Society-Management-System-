-- SUPABASE SQL SCHEMA FOR OCSMS
-- Run this entire script in your Supabase SQL Editor

-- 1. Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_locked BOOLEAN DEFAULT false,
    failed_attempts INT DEFAULT 0,
    lock_time BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- Insert Default Admin
INSERT INTO users (roll_number, name, email, password_hash, role)
VALUES ('00A-0000', 'University Admin', 'admin@fast.edu.pk', 'Password1', 'UNIVERSITY_ADMIN');

-- 2. Societies Table
CREATE TABLE societies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    capacity INT NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 3. Society Presidents (Mapping Table)
CREATE TABLE society_presidents (
    society_id UUID REFERENCES societies(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (society_id, user_id)
);

-- 4. Memberships & Join Requests Table
CREATE TABLE memberships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID REFERENCES users(id) ON DELETE CASCADE,
    society_id UUID REFERENCES societies(id) ON DELETE CASCADE,
    motivation_statement TEXT,
    remarks TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    applied_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 5. Events Table
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    venue VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    event_type VARCHAR(50),
    society_id UUID REFERENCES societies(id) ON DELETE CASCADE,
    poster_path TEXT,
    is_joint_event BOOLEAN DEFAULT false,
    status VARCHAR(50) DEFAULT 'UPCOMING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 6. Event Registrations Table
CREATE TABLE event_registrations (
    event_id UUID REFERENCES events(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, user_id)
);

-- 7. Attendance Table
CREATE TABLE attendance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID REFERENCES events(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    marked_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);

-- 8. Finance (Budget & Slips) Table
CREATE TABLE finance_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id UUID REFERENCES societies(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    entry_date DATE NOT NULL,
    category VARCHAR(50) NOT NULL, -- INCOME or EXPENSE
    receipt_path TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())
);
