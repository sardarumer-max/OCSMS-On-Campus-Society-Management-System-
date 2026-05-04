-- Run this in your Supabase SQL Editor to allow the Java App to read and write data

-- 1. Disable Row Level Security (RLS) on all tables so your Java app can freely read/write
ALTER TABLE users DISABLE ROW LEVEL SECURITY;
ALTER TABLE societies DISABLE ROW LEVEL SECURITY;
ALTER TABLE society_presidents DISABLE ROW LEVEL SECURITY;
ALTER TABLE memberships DISABLE ROW LEVEL SECURITY;
ALTER TABLE events DISABLE ROW LEVEL SECURITY;
ALTER TABLE event_registrations DISABLE ROW LEVEL SECURITY;
ALTER TABLE attendance DISABLE ROW LEVEL SECURITY;
ALTER TABLE finance_entries DISABLE ROW LEVEL SECURITY;

-- 2. Ensure the University Admin exists!
INSERT INTO users (roll_number, name, email, password_hash, role)
VALUES ('00A-0000', 'University Admin', 'admin@fast.edu.pk', 'Password1', 'UNIVERSITY_ADMIN')
ON CONFLICT (roll_number) DO NOTHING;
