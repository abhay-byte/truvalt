# Truvalt Development Workflow

## Task Assignment → Completion Cycle

### 1. Task Assignment
When a new task/issue/feature is assigned:

```bash
# Update progress tracking
# Move task from TODO.md to ONGOING.md with start date
```

**Files to update:**
- `/docs/progress/TODO.md` - Remove task
- `/docs/progress/ONGOING.md` - Add task with start date and progress

---

### 2. Implementation
Work on the task following project structure:

**Android:** `/android/app/src/main/java/com/ivarna/truvalt/`
**Docs:** `/docs/`

---

### 3. Build APK
After implementation complete:

```bash
cd ~/repos/Truvalt/android
./gradlew assembleDebug

# Copy APK to Android downloads folder (proot-distro Debian in Termux)
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/Truvalt-$(date +%Y%m%d-%H%M).apk
```

---

### 4. Update Documentation
Update all affected docs:

**Always update:**
- `/docs/progress/ONGOING.md` → `/docs/progress/FINISHED.md` (move completed task)
- `/docs/README.md` (update "Last Updated" dates)

**Update if changed:**
- `/docs/FEATURES.md` (feature status: 🔴→🟡→🟢)
- `/docs/SRS.md` (new requirements)
- `/docs/SDD.md` (architecture/API/database changes)
- `/docs/UI_UX_DOCUMENTATION.md` (new/modified screens)
- `/docs/DIAGRAMS.md` (architecture/flow changes)

---

### 5. Commit & Push

```bash
cd ~/repos/Truvalt

# Stage all changes
git add .

# Commit with descriptive message
git commit -m "feat: [TASK-ID] Brief description

- Implementation detail 1
- Implementation detail 2
- Updated docs and progress tracking"

# Push to remote
git push origin main
```

---

## Quick Reference

### Task States
- 🔴 Not Started (in TODO.md)
- 🟡 In Progress (in ONGOING.md)
- 🟢 Complete (in FINISHED.md)

### APK Location
- **Build output:** `android/app/build/outputs/apk/debug/app-debug.apk`
- **Copy to:** `/sdcard/Download/Truvalt-YYYYMMDD-HHMM.apk`

### Commit Message Format
```
<type>: [TASK-ID] <subject>

<body>
```

**Types:** `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

---

## Example Full Cycle

```bash
# 1. Update progress (manual edit)
# Move TASK-018 from TODO.md to ONGOING.md

# 2. Implement feature
# ... code changes ...

# 3. Build APK
cd ~/repos/Truvalt/android
./gradlew assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/Truvalt-$(date +%Y%m%d-%H%M).apk

# 4. Update docs (manual edit)
# Move TASK-018 from ONGOING.md to FINISHED.md
# Update FEATURES.md, SDD.md, etc.

# 5. Commit & push
cd ~/repos/Truvalt
git add .
git commit -m "feat: [TASK-018] Implement cloud sync

- Added SyncManager with delta sync
- Updated VaultRepository with sync endpoints
- Added conflict resolution (last-write-wins)
- Updated SDD.md with sync architecture
- Moved TASK-018 to FINISHED.md"
git push origin main
```
