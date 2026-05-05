# Admin Audit Management Design

## Overview

Change admin management from "edit content" to "audit (approve/reject) + delete" for users, guides, stories, and comments. Admins should moderate content, not modify it.

## Current State

- **User:** Has `status` field (1=normal, 0=disabled). Admin can edit username/phone/email/status.
- **Guide/Story/Comment:** No status field. Admin can edit content.
- **Destination:** Unchanged — admin keeps edit capability (destinations are site-managed, not user-generated).

## Design

### 1. Status Values

| Entity | New Status Values |
|--------|-------------------|
| User | 0=待审核, 1=正常, 2=禁用 |
| Guide | 0=待审核, 1=已通过, 2=已拒绝, 3=已下架 |
| Story | 0=待审核, 1=已通过, 2=已拒绝, 3=已下架 |
| Comment | 0=待审核, 1=已通过, 2=已拒绝, 3=已下架 |

### 2. Admin Operations

#### User Management
- **Audit:** Approve (0→1), Reject (0→2)
- **Disable:** Disable active user (1→2)
- **Enable:** Re-enable disabled user (2→1)
- **Delete:** Remove user permanently
- **Remove:** Edit username/phone/email

#### Guide/Story/Comment Management
- **Audit:** Approve (0→1), Reject (0→2)
- **Down:** Take down published content (1→3)
- **Delete:** Remove permanently
- **Remove:** Edit content

### 3. Backend Changes

#### New Audit Endpoint

```
POST /admin/{entity}/{id}/audit
Body: { "action": "approve" | "reject" | "disable" | "enable" | "down" }
```

- `approve`: Set status to 1
- `reject`: Set status to 2
- `disable` (User only): Set status to 2
- `enable` (User only): Set status to 1
- `down` (Guide/Story/Comment only): Set status to 3

#### Remove Edit Endpoints

Remove these PUT endpoints:
- `PUT /admin/users/{id}` (edit user info)
- `PUT /admin/guides/{id}` (edit guide title/summary)
- `PUT /admin/stories/{id}` (edit story content)

Keep: `PUT /admin/destinations/{id}` (destinations are site-managed)

#### Add Status Filter

Add `?status=` query parameter to list endpoints:
- `GET /admin/users?status=0` — list pending users
- `GET /admin/guides?status=1` — list approved guides
- `GET /admin/comments?status=0` — list pending comments

### 4. Database Changes

#### Users Table
```sql
ALTER TABLE users MODIFY COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=正常, 2=禁用';
```

#### Guides/Stories/Comments Tables
```sql
ALTER TABLE guides ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架';
ALTER TABLE stories ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架';
ALTER TABLE comments ADD COLUMN status INT DEFAULT 0 COMMENT '0=待审核, 1=已通过, 2=已拒绝, 3=已下架';
```

### 5. Frontend Changes

#### List Pages
- Add status filter tabs/buttons (全部/待审核/已通过/已拒绝/已下架)
- Show status badge on each row
- Replace "编辑" button with action buttons:
  - 待审核: [通过] [拒绝]
  - 已通过: [下架]
  - 已拒绝: [通过]
  - 已下架: [通过]

#### Remove Edit Routes
- Remove `#/users/edit/:id`
- Remove `#/guides/edit/:id`
- Remove `#/stories/edit/:id`

#### User List Special
- Add enable/disable toggle button
- Status values map to Chinese labels: 0=待审核, 1=正常, 2=禁用

### 6. Entity Model Changes

#### User.java
- Change status comment: `0=待审核, 1=正常, 2=禁用`

#### GuideSummary.java / GuideDetail.java
- Add `status` (Integer) field

#### GuideStoryVO.java
- Add `status` (Integer) field

#### GuideComment.java
- Add `status` (Integer) field

### 7. Mapper Changes

#### GuideMapper.java
- Add `updateStatus(Long id, Integer status)` method
- Update `selectPage` to include `?status=` filter
- Update `countAll` to include `?status=` filter
- Update XML: add `g.status` to SELECT columns, add status WHERE clause

#### GuideStoryMapper.java
- Add `updateStatus(Long id, Integer status)` method
- Update `selectAll` to include `?status=` filter
- Update `countAll` to include `?status=` filter
- Update XML: add `status` to SELECT columns, add status WHERE clause

#### GuideCommentMapper.java
- Add `updateStatus(Long id, Integer status)` method
- Update `selectPage` to include `?status=` filter
- Update `countAll` to include `?status=` filter
- Update XML: add `c.status` to SELECT columns, add status WHERE clause

#### UserMapper.java (if exists)
- Add `updateStatus(Long id, Integer status)` method
- Add `countByStatus(Integer status)` method

### 8. Admin Controller Changes

#### AdminGuideApiController.java
- Remove `PUT /{id}` (updateGuide)
- Add `POST /{id}/audit` endpoint
- Update `GET /` to accept `?status=` filter

#### AdminStoryApiController.java
- Remove `PUT /{id}` (updateContent)
- Add `POST /{id}/audit` endpoint
- Update `GET /` to accept `?status=` filter

#### AdminCommentApiController.java
- Add `POST /{id}/audit` endpoint
- Update `GET /` to accept `?status=` filter

#### AdminUserApiController.java
- Remove `PUT /{id}` (update user info)
- Add `POST /{id}/audit` endpoint
- Update `GET /` to accept `?status=` filter

### 9. Admin Log Changes

Log audit actions with action type "AUDIT" instead of "UPDATE":
```
action: AUDIT
targetType: USER/GUIDE/STORY/COMMENT
targetId: {id}
detail: "审核通过" / "审核拒绝" / "禁用" / "下架"
```

## Files to Modify

### Backend - Controllers
- `AdminUserApiController.java` — remove PUT, add audit endpoint
- `AdminGuideApiController.java` — remove PUT, add audit endpoint
- `AdminStoryApiController.java` — remove PUT, add audit endpoint
- `AdminCommentApiController.java` — add audit endpoint

### Backend - Models
- `User.java` — update status comment
- `GuideSummary.java` — add status field
- `GuideDetail.java` — add status field
- `GuideStoryVO.java` — add status field
- `GuideComment.java` — add status field

### Backend - Mappers
- `GuideMapper.java` — add updateStatus, update selectPage/countAll for status filter
- `GuideMapper.xml` — add status to SELECT, add status WHERE clause
- `GuideStoryMapper.java` — add updateStatus, update selectAll/countAll for status filter
- `GuideStoryMapper.xml` — add status to SELECT, add status WHERE clause
- `GuideCommentMapper.java` — add updateStatus, update selectPage/countAll for status filter
- `GuideCommentMapper.xml` — add status to SELECT, add status WHERE clause

### Frontend (SPA)
- `js/pages/users.js` — replace edit with audit actions
- `js/pages/guides.js` — replace edit with audit actions
- `js/pages/stories.js` — replace edit with audit actions
- `js/pages/comments.js` — add audit actions
- `js/api.js` — add audit API method
- `index.html` — remove edit routes

### Database
- SQL migration script for status column changes

## Out of Scope

- Destination management (stays as edit+delete)
- Bulk audit operations
- Audit history viewing in admin panel
- Content preview before audit
