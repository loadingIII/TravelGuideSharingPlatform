<template>
  <div class="user-profile-page">
    <!-- 顶部导航 -->
    <header class="profile-header">
      <div class="container">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回</span>
        </button>
        <h1 class="page-title">个人中心</h1>
        <div class="header-spacer"></div>
      </div>
    </header>

    <div class="profile-container">
      <!-- 左侧：用户信息卡片 -->
      <aside class="user-sidebar">
        <div class="user-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img 
                v-if="userInfo.avatar" 
                :src="userInfo.avatar" 
                alt="用户头像" 
                class="user-avatar-img"
                :class="{ 'clickable': !isEditing }"
                @click="handleAvatarClick"
              >
              <div 
                v-else 
                class="avatar-placeholder"
                :class="{ 'clickable': !isEditing }"
                @click="handleAvatarClick"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <button 
                v-if="isEditing" 
                class="edit-avatar-btn" 
                @click="triggerAvatarUpload" 
                title="更换头像"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
                  <circle cx="12" cy="13" r="4"/>
                </svg>
              </button>
              <input ref="avatarInput" type="file" accept="image/*" style="display: none" @change="handleAvatarChange">
            </div>
            <h2 class="user-name">{{ userInfo.username }}</h2>
            <p class="user-role">{{ userInfo.nickname || '旅行爱好者' }}</p>
          </div>

          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ stats.guides }}</span>
              <span class="stat-label">发布攻略</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.stories }}</span>
              <span class="stat-label">旅行故事</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.likes }}</span>
              <span class="stat-label">获赞</span>
            </div>
            <div class="stat-item clickable" @click="showFollowList('followers')">
              <span class="stat-value">{{ stats.followers }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item clickable" @click="showFollowList('following')">
              <span class="stat-value">{{ stats.following }}</span>
              <span class="stat-label">关注</span>
            </div>
          </div>

          <div class="user-menu">
            <button 
              v-for="item in menuItems" 
              :key="item.key"
              class="menu-item"
              :class="{ active: currentTab === item.key }"
              @click="currentTab = item.key"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-html="item.icon"></svg>
              <span>{{ item.label }}</span>
            </button>
          </div>
        </div>
      </aside>

      <!-- 右侧：内容区域 -->
      <main class="profile-content">
        <!-- 个人信息 -->
        <section v-if="currentTab === 'profile'" class="content-section">
          <div class="section-header">
            <h3>个人信息</h3>
            <button class="edit-btn" @click="isEditing = !isEditing">
              {{ isEditing ? '取消' : '编辑资料' }}
            </button>
          </div>
          
          <div class="profile-form">
            <div class="form-group">
              <label>用户名</label>
              <input v-model="formData.username" type="text" :disabled="!isEditing" placeholder="请输入用户名">
            </div>
            <div class="form-group">
              <label>昵称</label>
              <input v-model="formData.nickname" type="text" :disabled="!isEditing" placeholder="请输入昵称">
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input v-model="formData.phone" type="tel" :disabled="!isEditing" placeholder="请输入手机号">
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input v-model="formData.email" type="email" :disabled="!isEditing" placeholder="请输入邮箱">
            </div>
            <div class="form-group">
              <label>个人简介</label>
              <textarea v-model="formData.bio" :disabled="!isEditing" rows="4" placeholder="介绍一下你自己..."></textarea>
            </div>
            <div v-if="isEditing" class="form-actions">
              <button class="btn btn-secondary" @click="isEditing = false">取消</button>
              <button class="btn btn-primary" @click="saveProfile">保存修改</button>
            </div>
          </div>
        </section>

        <!-- 我的发布 -->
        <section v-if="currentTab === 'posts'" class="content-section">
          <div class="section-header">
            <h3>我的发布</h3>
            <div class="filter-tabs">
              <button 
                v-for="tab in postTabs" 
                :key="tab.key"
                class="filter-tab"
                :class="{ active: currentPostTab === tab.key }"
                @click="currentPostTab = tab.key"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- 攻略列表 -->
          <div v-if="currentPostTab === 'guides'" class="content-list">
            <div v-for="guide in myGuides" :key="guide.id" class="content-card">
              <div class="card-image">
                <img :src="guide.coverImageUrl || '/img/富士山.jpg'" alt="攻略封面">
              </div>
              <div class="card-body">
                <h4 class="card-title">{{ guide.title }}</h4>
                <p class="card-desc">{{ guide.summary }}</p>
                <div class="card-meta">
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                    {{ guide.viewsCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ guide.likesCount || 0 }}
                  </span>
                  <span class="meta-item">{{ formatDate(guide.publishedAt) }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn edit" @click="editGuide(guide.id)" title="编辑">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button class="action-btn delete" @click="deleteGuide(guide.id)" title="删除">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="myGuides.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <polyline points="10 9 9 9 8 9"/>
              </svg>
              <p>还没有发布攻略</p>
              <button class="btn btn-primary" @click="createGuide">写攻略</button>
            </div>
          </div>

          <!-- 故事列表 -->
          <div v-if="currentPostTab === 'stories'" class="content-list">
            <div v-for="story in myStories" :key="story.id" class="content-card story-card">
              <div class="card-body">
                <p class="story-content">{{ story.content }}</p>
                <div v-if="story.images && story.images.length" class="story-images">
                  <img v-for="(img, idx) in story.images.slice(0, 3)" :key="idx" :src="img" alt="故事图片">
                  <div v-if="story.images.length > 3" class="more-images">+{{ story.images.length - 3 }}</div>
                </div>
                <div class="card-meta">
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ story.likesCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
                    </svg>
                    {{ story.comments_count || 0 }}
                  </span>
                  <span class="meta-item">{{ formatDate(story.created_at) }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn edit" @click="editStory(story.id)" title="编辑">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button class="action-btn delete" @click="deleteStory(story.id)" title="删除">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="myStories.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 20h9"/>
                <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
              </svg>
              <p>还没有发布故事</p>
              <button class="btn btn-primary" @click="createStory">写故事</button>
            </div>
          </div>
        </section>

        <!-- 我的评论 -->
        <section v-if="currentTab === 'comments'" class="content-section">
          <div class="section-header">
            <h3>我的评论</h3>
          </div>
          <div class="content-list">
            <div v-for="comment in myComments" :key="comment.id" class="content-card comment-card">
              <div class="comment-header">
                <span class="comment-target">评论了攻略《{{ comment.guideTitle || '未知攻略' }}》</span>
                <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
              <div class="comment-footer">
                <div class="comment-stats">
                  <span class="stat-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ comment.likesCount || 0 }}
                  </span>
                </div>
                <div class="card-actions">
                  <button class="action-btn delete" @click="deleteComment(comment.id)" title="删除">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
            <div v-if="myComments.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
              </svg>
              <p>还没有发表评论</p>
            </div>
          </div>
        </section>

        <!-- 点赞收藏 -->
        <section v-if="currentTab === 'favorites'" class="content-section">
          <div class="section-header">
            <h3>点赞收藏</h3>
            <div class="filter-tabs">
              <button 
                v-for="tab in favoriteTabs" 
                :key="tab.key"
                class="filter-tab"
                :class="{ active: currentFavoriteTab === tab.key }"
                @click="currentFavoriteTab = tab.key"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- 点赞的攻略 -->
          <div v-if="currentFavoriteTab === 'likedGuides'" class="content-list">
            <div v-for="guide in likedGuides" :key="guide.id" class="content-card">
              <div class="card-image">
                <img :src="guide.coverImageUrl || '/img/富士山.jpg'" alt="攻略封面">
              </div>
              <div class="card-body">
                <h4 class="card-title">{{ guide.title }}</h4>
                <p class="card-desc">{{ guide.summary }}</p>
                <div class="card-author">
                  <img :src="guide.authorAvatarUrl || '/img/头像1.jpg'" alt="作者">
                  <span>{{ guide.authorName }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn unlike" @click="unlikeGuide(guide.id)" title="取消点赞">
                  <svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="likedGuides.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              <p>还没有点赞任何攻略</p>
            </div>
          </div>

          <!-- 收藏的故事 -->
          <div v-if="currentFavoriteTab === 'savedStories'" class="content-list">
            <div v-for="story in savedStories" :key="story.id" class="content-card story-card">
              <div class="card-body">
                <div class="story-author">
                  <img :src="story.author_avatar || '/img/头像1.jpg'" alt="作者">
                  <span>{{ story.author_name }}</span>
                </div>
                <p class="story-content">{{ story.content }}</p>
                <div v-if="story.images && story.images.length" class="story-images">
                  <img v-for="(img, idx) in story.images.slice(0, 3)" :key="idx" :src="img" alt="故事图片">
                </div>
              </div>
              <div class="card-actions">
                <button class="action-btn unlike" @click="unsaveStory(story.id)" title="取消收藏">
                  <svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                    <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                  </svg>
                </button>
              </div>
            </div>
            <div v-if="savedStories.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
              </svg>
              <p>还没有收藏任何故事</p>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- 头像放大预览弹窗 -->
    <div v-if="showAvatarPreview" class="avatar-preview-modal" @click="closeAvatarPreview">
      <div class="avatar-preview-content" @click.stop>
        <button class="close-preview-btn" @click="closeAvatarPreview">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
        <img 
          v-if="userInfo.avatar" 
          :src="userInfo.avatar" 
          alt="用户头像" 
          class="preview-avatar-img"
        >
        <div v-else class="preview-avatar-placeholder">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
      </div>
    </div>
    <ConfirmDialog ref="confirmRef" />

    <!-- 编辑攻略弹窗 -->
    <div class="modal-overlay" v-if="showEditModal && editingGuide" @click.self="showEditModal = false">
      <div class="edit-modal">
        <div class="edit-modal-header">
          <h3>编辑攻略</h3>
          <button class="edit-close" @click="showEditModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="edit-modal-body">
          <div class="edit-field">
            <label>标题</label>
            <input type="text" v-model="editingGuide.title" placeholder="给你的攻略起个吸引人的标题">
          </div>
          <div class="edit-field">
            <label>简介</label>
            <input type="text" v-model="editingGuide.summary" placeholder="一句话描述你的攻略亮点">
          </div>
          <div class="edit-form-row">
            <div class="edit-field">
              <label>目的地</label>
              <input type="text" v-model="editingGuide.locationText" placeholder="如：东京、巴黎、清迈">
            </div>
            <div class="edit-field">
              <label>封面图片</label>
              <div class="edit-upload-area" @click="triggerEditCoverUpload">
                <img v-if="editCoverPreview || editingGuide.coverImageUrl" :src="editCoverPreview || editingGuide.coverImageUrl" class="edit-cover-preview">
                <div v-else class="edit-upload-placeholder">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                  </svg>
                  <span>点击上传封面图片</span>
                </div>
                <input type="file" ref="editCoverInput" accept="image/*" style="display: none" @change="handleEditCoverUpload">
              </div>
            </div>
          </div>
          <div class="edit-form-row">
            <div class="edit-field">
              <label>旅行范围</label>
              <select v-model="editingGuide.scope">
                <option value="international">出境游</option>
                <option value="domestic">国内游</option>
              </select>
            </div>
            <div class="edit-field">
              <label>旅行方式</label>
              <select v-model="editingGuide.travelMode">
                <option value="free">自由行</option>
                <option value="group">跟团游</option>
                <option value="family">亲子游</option>
                <option value="honeymoon">蜜月游</option>
              </select>
            </div>
          </div>
          <div class="edit-field">
            <label>行程概览</label>
            <div class="edit-itinerary-list">
              <div v-for="(day, index) in editingGuide.itineraryDays" :key="index" class="edit-itinerary-item">
                <div class="edit-itinerary-header">
                  <span class="edit-day-badge">Day {{ day.dayNo }}</span>
                  <button class="edit-remove-btn" @click="removeEditDay(index)" v-if="editingGuide.itineraryDays.length > 1">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
                  </button>
                </div>
                <input type="text" v-model="day.title" placeholder="行程标题，如：抵达东京 & 浅草寺">
                <input type="text" v-model="day.summary" placeholder="简短描述，如：感受传统日式文化">
                <div class="edit-spots-list" v-if="day.spots && day.spots.length > 0">
                  <div v-for="(spot, sIdx) in day.spots" :key="sIdx" class="edit-spot-item">
                    <div class="edit-spot-top">
                      <div class="edit-spot-img-upload" @click="triggerEditSpotImg(index, sIdx)">
                        <img v-if="spot.imageUrl" :src="spot.imageUrl" class="edit-spot-img-preview">
                        <div v-else class="edit-spot-img-placeholder">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                          </svg>
                          <span>图片</span>
                        </div>
                      </div>
                      <div class="edit-spot-fields">
                        <input type="text" v-model="spot.name" placeholder="景点名称">
                        <input type="text" v-model="spot.description" placeholder="景点描述">
                        <div class="edit-spot-row">
                          <input type="text" v-model="spot.time" placeholder="时间 09:00">
                          <input type="text" v-model="spot.duration" placeholder="时长 2小时">
                        </div>
                      </div>
                    </div>
                    <button class="edit-remove-spot" @click="removeEditSpot(index, sIdx)">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
                    </button>
                  </div>
                </div>
                <button class="edit-add-spot" @click="addEditSpot(index)">+ 添加景点</button>
              </div>
              <button class="edit-add-day" @click="addEditDay">+ 添加一天</button>
            </div>
          </div>
          <div class="edit-field">
            <label>详细攻略</label>
            <RichEditor v-model="editingGuide.contentHtml" placeholder="分享你的详细旅行经历、攻略心得..." />
          </div>
        </div>
        <div class="edit-modal-footer">
          <button class="btn-cancel" @click="showEditModal = false">取消</button>
          <button class="btn-save" @click="saveGuide">保存</button>
        </div>
      </div>
    </div>

    <!-- 粉丝/关注列表弹窗 -->
    <div class="modal-overlay" v-if="showFollowModal" @click.self="closeFollowModal">
      <div class="modal-content follow-modal">
        <div class="modal-header">
          <h3>{{ followListType === 'followers' ? '粉丝' : '关注' }}</h3>
          <button class="close-btn" @click="closeFollowModal">&times;</button>
        </div>
        <div class="follow-modal-body">
          <div v-if="followListLoading" class="loading">加载中...</div>
          <div v-else-if="followList.length === 0" class="empty">暂无数据</div>
          <div v-else class="follow-list">
            <div v-for="user in followList" :key="user.userId" class="follow-item" @click="viewPublicProfile(user.userId)">
              <img :src="user.avatarUrl || '/img/头像1.jpg'" class="follow-avatar" @error="e => e.target.src = '/img/头像1.jpg'">
              <span class="follow-name">{{ user.nickname || '用户' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, inject } from 'vue'
import request from '../utils/request.js'
import ConfirmDialog from './ConfirmDialog.vue'
import RichEditor from './RichEditor.vue'

const toast = inject('toast')
const confirmRef = ref(null)

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['back', 'update-user', 'view-my-guides', 'view-public-profile'])

// 当前选中的标签页
const currentTab = ref('profile')
const currentPostTab = ref('guides')
const currentFavoriteTab = ref('likedGuides')
const isEditing = ref(false)
const avatarInput = ref(null)
const showAvatarPreview = ref(false)

// 菜单项
const menuItems = [
  {
    key: 'profile',
    label: '个人信息',
    icon: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>'
  },
  {
    key: 'posts',
    label: '我的发布',
    icon: '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>'
  },
  {
    key: 'comments',
    label: '我的评论',
    icon: '<path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>'
  },
  {
    key: 'favorites',
    label: '点赞收藏',
    icon: '<path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>'
  }
]

// 发布标签
const postTabs = [
  { key: 'guides', label: '攻略' },
  { key: 'stories', label: '故事' }
]

// 收藏标签
const favoriteTabs = [
  { key: 'likedGuides', label: '点赞的攻略' },
  { key: 'savedStories', label: '收藏的故事' }
]

// 统计数据
const stats = reactive({
  guides: 0,
  stories: 0,
  likes: 0,
  followers: 0,
  following: 0
})

// 表单数据
const formData = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  bio: ''
})

// 监听 props.userInfo 变化，同步更新表单数据
watch(() => props.userInfo, (newUserInfo) => {
  if (newUserInfo) {
    formData.username = newUserInfo.username || ''
    formData.nickname = newUserInfo.nickname || ''
    formData.phone = newUserInfo.phone || ''
    formData.email = newUserInfo.email || ''
    formData.bio = newUserInfo.bio || ''
  }
}, { immediate: true, deep: true })

// 数据列表
const myGuides = ref([])
const myStories = ref([])
const myComments = ref([])
const likedGuides = ref([])
const savedStories = ref([])

// 粉丝/关注
const showFollowModal = ref(false)
const followListType = ref('followers')
const followList = ref([])
const followListLoading = ref(false)

const showFollowList = async (type) => {
  followListType.value = type
  followList.value = []
  showFollowModal.value = true
  followListLoading.value = true
  try {
    const res = await request.get(`/api/users/${props.userInfo?.id}/follow${type === 'followers' ? 'ers' : 'ing'}`)
    if (res.code === 'OK') {
      followList.value = res.data || []
    }
  } catch (err) {
    console.error('加载关注列表失败:', err)
  } finally {
    followListLoading.value = false
  }
}

const closeFollowModal = () => {
  showFollowModal.value = false
  followList.value = []
}

const viewPublicProfile = (userId) => {
  closeFollowModal()
  emit('view-public-profile', userId)
}

// 返回上一页
const goBack = () => {
  emit('back')
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像点击
const handleAvatarClick = () => {
  if (!isEditing.value) {
    openAvatarPreview()
  }
}

// 打开头像预览
const openAvatarPreview = () => {
  showAvatarPreview.value = true
  document.body.style.overflow = 'hidden'
}

// 关闭头像预览
const closeAvatarPreview = () => {
  showAvatarPreview.value = false
  document.body.style.overflow = ''
}

// 处理头像更换
const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 先显示本地预览
  const reader = new FileReader()
  reader.onload = (event) => {
    // 通过 emit 通知父组件更新头像（本地预览）
    emit('update-user', { ...props.userInfo, avatar: event.target.result })
  }
  reader.readAsDataURL(file)

  // 上传到服务器
  try {
    const formData = new FormData()
    formData.append('file', file)

    const res = await request.post('/api/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    console.log('上传响应:', res) // 调试用

    if (res.code === 'OK' || res.code === 200) {
      // 上传成功，使用服务器返回的URL
      emit('update-user', { ...props.userInfo, avatar: res.data })
      alert('头像上传成功！')
    } else {
      alert('上传失败：' + res.message)
    }
  } catch (error) {
    console.error('上传失败:', error)
    alert('头像上传失败，请稍后重试')
  }
}

// 保存个人资料
const saveProfile = async () => {
  try {
    // 构建请求数据，包含表单信息和头像地址
    const profileData = {
      nickname: formData.nickname,
      email: formData.email || null,
      bio: formData.bio,
      avatarUrl: props.userInfo.avatar
    }

    const res = await request.put('/api/users/me/profile', profileData)

    if (res.code === 'OK' || res.code === 200) {
      // 更新成功，通知父组件更新用户信息
      emit('update-user', { ...props.userInfo, ...profileData })
      isEditing.value = false
      alert('个人资料保存成功！')
    } else {
      alert('保存失败：' + res.message)
    }
  } catch (error) {
    console.error('保存个人资料失败:', error)
    alert('保存失败，请稍后重试')
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 编辑攻略
const editGuide = async (id) => {
  try {
    const res = await request.get(`/api/guides/${id}`)
    if (res.code === 'OK' || res.code === 200) {
      const d = res.data
      editingGuide.value = {
        id: d.id,
        title: d.title || '',
        summary: d.summary || '',
        contentHtml: d.contentHtml || '',
        coverImageUrl: d.coverImageUrl || '',
        locationText: d.locationText || '',
        scope: d.scope || 'international',
        travelMode: d.travelMode || 'free',
        itineraryDays: (d.itinerary || []).map(day => ({
          dayNo: day.dayNo,
          title: day.title || '',
          summary: day.summary || '',
          spots: (day.spots || []).map(spot => ({
            name: spot.name || '',
            description: spot.description || '',
            imageUrl: spot.imageUrl || '',
            _imageFile: null,
            time: spot.time || '',
            duration: spot.duration || ''
          }))
        }))
      }
      editCoverPreview.value = ''
      showEditModal.value = true
    }
  } catch (err) {
    toast.error('获取攻略详情失败')
  }
}

const showEditModal = ref(false)
const editingGuide = ref(null)
const editCoverPreview = ref('')
const editCoverInput = ref(null)

const triggerEditCoverUpload = () => editCoverInput.value?.click()
const handleEditCoverUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    editingGuide.value._coverFile = file
    editCoverPreview.value = URL.createObjectURL(file)
  }
}

const addEditDay = () => {
  const lastDay = editingGuide.value.itineraryDays
  editingGuide.value.itineraryDays.push({
    dayNo: lastDay.length + 1,
    title: '',
    summary: '',
    spots: []
  })
}

const removeEditDay = (index) => {
  editingGuide.value.itineraryDays.splice(index, 1)
}

const addEditSpot = (dayIndex) => {
  editingGuide.value.itineraryDays[dayIndex].spots.push({
    name: '', description: '', imageUrl: '', _imageFile: null, time: '', duration: ''
  })
}

const removeEditSpot = (dayIndex, spotIndex) => {
  editingGuide.value.itineraryDays[dayIndex].spots.splice(spotIndex, 1)
}

const triggerEditSpotImg = (dayIndex, spotIndex) => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      editingGuide.value.itineraryDays[dayIndex].spots[spotIndex]._imageFile = file
      editingGuide.value.itineraryDays[dayIndex].spots[spotIndex].imageUrl = URL.createObjectURL(file)
    }
  }
  input.click()
}

const saveGuide = async () => {
  const g = editingGuide.value
  if (!g.title?.trim()) { toast.warning('请输入标题'); return }
  try {
    // 上传封面图片
    let coverImageUrl = g.coverImageUrl
    if (g._coverFile) {
      const fd = new FormData()
      fd.append('file', g._coverFile)
      const uploadRes = await request.post('/api/files/upload', fd, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      if (uploadRes.code === 'OK' || uploadRes.code === 200) {
        coverImageUrl = uploadRes.data
      }
    }

    // 重新编号 dayNo，上传景点图片
    const itineraryDays = []
    for (const day of g.itineraryDays.filter(d => d.title?.trim())) {
      const spots = []
      for (const spot of (day.spots || []).filter(s => s.name?.trim())) {
        let spotImageUrl = spot.imageUrl || null
        if (spot._imageFile) {
          const fd = new FormData()
          fd.append('file', spot._imageFile)
          const res = await request.post('/api/files/upload', fd, {
            headers: { 'Content-Type': 'multipart/form-data' }
          })
          if (res.code === 'OK' || res.code === 200) {
            spotImageUrl = res.data
          }
        }
        spots.push({
          name: spot.name,
          description: spot.description,
          imageUrl: spotImageUrl,
          time: spot.time,
          duration: spot.duration
        })
      }
      itineraryDays.push({
        dayNo: itineraryDays.length + 1,
        title: day.title,
        summary: day.summary,
        spots
      })
    }

    const res = await request.put(`/api/guides/${g.id}`, {
      title: g.title,
      summary: g.summary,
      contentHtml: g.contentHtml,
      coverImageUrl: coverImageUrl,
      locationText: g.locationText,
      scope: g.scope,
      travelMode: g.travelMode,
      itineraryDays: itineraryDays
    })
    if (res.code === 'OK' || res.code === 200) {
      showEditModal.value = false
      await loadMyData()
      toast.success('攻略已更新')
    }
  } catch (err) {
    toast.error('更新失败')
  }
}

// 删除攻略
const deleteGuide = async (id) => {
  const confirmed = await confirmRef.value?.show('确定要删除这篇攻略吗？删除后无法恢复')
  if (!confirmed) return
  try {
    const res = await request.delete(`/api/guides/${id}`)
    if (res.code === 'OK' || res.code === 200) {
      myGuides.value = myGuides.value.filter(g => g.id !== id)
      stats.guides = myGuides.value.length
      toast.success('攻略已删除')
    }
  } catch (err) {
    toast.error('删除失败')
  }
}

// 创建攻略
const createGuide = () => {
  console.log('创建攻略')
}

// 编辑故事
const editStory = (id) => {
  console.log('编辑故事:', id)
}

// 删除故事
const deleteStory = async (id) => {
  if (!confirm('确定要删除这个故事吗？')) return
  try {
    const res = await request.delete(`/api/stories/${id}`)
    if (res.code === 'OK' || res.code === 200) {
      myStories.value = myStories.value.filter(s => s.id !== id)
      stats.stories = myStories.value.length
    }
  } catch (err) {
    console.error('删除故事失败:', err)
  }
}

// 创建故事
const createStory = () => {
  console.log('创建故事')
}

// 删除评论
const deleteComment = async (id) => {
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    const res = await request.delete(`/api/guides/comments/${id}`)
    if (res.code === 'OK' || res.code === 200) {
      myComments.value = myComments.value.filter(c => c.id !== id)
    }
  } catch (err) {
    console.error('删除评论失败:', err)
  }
}

// 取消点赞
const unlikeGuide = async (id) => {
  try {
    const res = await request.delete(`/api/guides/${id}/like`)
    if (res.code === 'OK' || res.code === 200) {
      likedGuides.value = likedGuides.value.filter(g => g.id !== id)
    }
  } catch (err) {
    console.error('取消点赞失败:', err)
  }
}

// 取消收藏
const unsaveStory = async (id) => {
  try {
    const res = await request.delete(`/api/guides/${id}/favorite`)
    if (res.code === 'OK' || res.code === 200) {
      savedStories.value = savedStories.value.filter(s => s.id !== id)
    }
  } catch (err) {
    console.error('取消收藏失败:', err)
  }
}

// 加载我的数据
const loadMyData = async () => {
  try {
    const [guidesRes, storiesRes, commentsRes, likedRes, favoritedRes, userRes] = await Promise.all([
      request.get('/api/users/me/guides?page=1&pageSize=50'),
      request.get('/api/users/me/stories?page=1&pageSize=50'),
      request.get('/api/users/me/comments?page=1&pageSize=50'),
      request.get('/api/users/me/liked-guides?page=1&pageSize=50'),
      request.get('/api/users/me/favorite-guides?page=1&pageSize=50'),
      request.get('/api/users/me')
    ])

    if (guidesRes.data?.list) {
      myGuides.value = guidesRes.data.list
      stats.guides = guidesRes.data.total || myGuides.value.length
    }
    if (storiesRes.data?.list) {
      myStories.value = storiesRes.data.list
      stats.stories = storiesRes.data.total || myStories.value.length
    }
    if (commentsRes.data?.list) {
      myComments.value = commentsRes.data.list
    }
    if (likedRes.data?.list) {
      likedGuides.value = likedRes.data.list
    }
    if (favoritedRes.data?.list) {
      savedStories.value = favoritedRes.data.list
    }
    stats.likes = myGuides.value.reduce((sum, g) => sum + (g.likesCount || 0), 0)
    if (userRes.code === 'OK' && userRes.data) {
      stats.followers = userRes.data.followersCount || 0
    }
    const followingRes = await request.get(`/api/users/${userRes.data?.id}/following`).catch(() => null)
    if (followingRes?.code === 'OK') {
      stats.following = followingRes.data?.length || 0
    }
  } catch (err) {
    console.error('加载用户数据失败:', err)
  }
}

onMounted(() => {
  loadMyData()
})
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--color-bg-primary) 0%, var(--color-bg-tertiary) 100%);
}

/* 顶部导航 */
.profile-header {
  background: var(--color-card-bg);
  border-bottom: 1px solid var(--color-card-border);
  box-shadow: 0 2px 10px rgba(45, 58, 30, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.profile-header .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  color: var(--color-primary);
}

.back-btn svg {
  width: 20px;
  height: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  font-family: var(--font-display);
}

.header-spacer {
  width: 60px;
}

/* 主容器 */
.profile-container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 30px;
}

/* 侧边栏 */
.user-sidebar {
  position: sticky;
  top: 90px;
  height: fit-content;
}

.user-card {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  padding: 30px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 25px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
}

.user-avatar-img,
.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  background: var(--color-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder svg {
  width: 50px;
  height: 50px;
  color: white;
}

.edit-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-primary);
  border: 3px solid var(--color-card-bg);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.edit-avatar-btn:hover {
  transform: scale(1.1);
  background: var(--color-primary-dark);
}

/* 头像可点击状态 */
.user-avatar-img.clickable,
.avatar-placeholder.clickable {
  cursor: zoom-in;
  transition: all 0.3s ease;
}

.user-avatar-img.clickable:hover,
.avatar-placeholder.clickable:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(245, 240, 232, 0.3);
}

.edit-avatar-btn svg {
  width: 16px;
  height: 16px;
  color: white;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 5px;
}

.user-role {
  font-size: 14px;
  color: var(--color-text-muted);
}

/* 统计 */
.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid var(--color-card-border);
  border-bottom: 1px solid var(--color-card-border);
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.stat-label {
  font-size: 12px;
  color: var(--color-text-muted);
}

/* 菜单 */
.user-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  color: var(--color-text-muted);
  font-size: 14px;
}

.menu-item svg {
  width: 20px;
  height: 20px;
}

.menu-item:hover {
  background: var(--color-bg-secondary);
  color: var(--color-primary);
}

.menu-item.active {
  background: var(--color-primary);
  color: #FFFFFF;
}

/* 内容区域 */
.profile-content {
  min-height: 600px;
}

.content-section {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  padding: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-card-border);
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.edit-btn {
  padding: 8px 20px;
  border-radius: 20px;
  border: 1px solid var(--color-primary);
  background: transparent;
  color: var(--color-primary);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background: var(--color-primary);
  color: #FFFFFF;
}

/* 表单 */
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-muted);
}

.form-group input,
.form-group textarea {
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-text-primary);
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #A8A29E;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #F5F0E8;
  box-shadow: 0 0 0 3px rgba(245, 240, 232, 0.1);
}

.form-group input:disabled,
.form-group textarea:disabled {
  background: rgba(255, 255, 255, 0.05);
  cursor: not-allowed;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 10px;
}

.btn {
  padding: 10px 24px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  color: var(--color-text-primary);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 240, 232, 0.4);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-text-muted);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.15);
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 6px 16px;
  border-radius: 20px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
}

.filter-tab:hover {
  background: rgba(255, 255, 255, 0.15);
}

.filter-tab.active {
  background: #F5F0E8;
  color: var(--color-text-primary);
}

/* 内容列表 */
.content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-card {
  display: flex;
  gap: 20px;
  padding: 20px 24px;
  background: var(--color-card-bg);
  border-radius: 16px;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid var(--color-card-border);
}

.content-card:hover {
  background: var(--color-card-bg-hover);
  border-color: var(--color-card-border);
  box-shadow: 0 4px 20px rgba(45, 58, 30, 0.1);
  transform: translateY(-2px);
}

.card-image {
  width: 140px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(45, 58, 30, 0.2);
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.content-card:hover .card-image img {
  transform: scale(1.05);
}

.card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  padding-right: 50px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.content-card:hover .card-title {
  color: var(--color-primary-dark);
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  line-height: 1.7;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
  flex: 1;
}

.card-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #A8A29E;
  margin-top: auto;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.3s ease;
}

.meta-item:hover {
  color: #F5F0E8;
}

.meta-item svg {
  width: 14px;
  height: 14px;
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: var(--color-text-muted);
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.edit:hover {
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #f87171;
}

.action-btn.unlike {
  color: #f87171;
}

.action-btn.unlike:hover {
  background: rgba(239, 68, 68, 0.2);
}

/* 故事卡片 */
.story-card {
  flex-direction: column;
  position: relative;
  padding: 20px 24px;
}

.story-card .card-body {
  width: 100%;
}

.story-content {
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.8;
  margin-bottom: 16px;
  padding-right: 50px;
}

.story-images {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.story-images img {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.story-images img:hover {
  transform: scale(1.05);
}

.more-images {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(45, 58, 30, 0.6) 0%, rgba(45, 58, 30, 0.4) 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.more-images:hover {
  background: linear-gradient(135deg, rgba(45, 58, 30, 0.7) 0%, rgba(45, 58, 30, 0.5) 100%);
}

.story-author,
.card-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.story-author img,
.card-author img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 2px 8px rgba(45, 58, 30, 0.2);
}

.story-author span,
.card-author span {
  font-size: 13px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.story-card .card-meta {
  display: flex;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.story-card .card-actions {
  position: absolute;
  right: 20px;
  top: 20px;
  flex-direction: row;
  gap: 8px;
}

/* 评论卡片 */
.comment-card {
  flex-direction: column;
  position: relative;
  padding: 20px 24px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comment-target {
  font-size: 13px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: var(--color-text-muted);
}

.comment-content {
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.8;
  margin-bottom: 16px;
  padding-right: 40px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-stats {
  display: flex;
  gap: 20px;
}

.comment-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-muted);
  transition: all 0.3s ease;
}

.comment-stats .stat-item svg {
  width: 16px;
  height: 16px;
  color: var(--color-text-muted);
}

.comment-stats .stat-item:hover {
  color: var(--color-primary);
}

.comment-card .card-actions {
  position: absolute;
  right: 20px;
  bottom: 20px;
  flex-direction: row;
  gap: 8px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--color-text-muted);
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 14px;
}

/* 头像预览弹窗 */
.avatar-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(45, 58, 30, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.avatar-preview-content {
  position: relative;
  max-width: 80vw;
  max-height: 80vh;
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from { 
    opacity: 0;
    transform: scale(0.9);
  }
  to { 
    opacity: 1;
    transform: scale(1);
  }
}

.preview-avatar-img {
  max-width: 400px;
  max-height: 400px;
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(45, 58, 30, 0.4);
}

.preview-avatar-placeholder {
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: linear-gradient(135deg, #F5F0E8 0%, #FAF8F5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20px 60px rgba(45, 58, 30, 0.4);
}

.preview-avatar-placeholder svg {
  width: 120px;
  height: 120px;
  color: white;
}

.close-preview-btn {
  position: absolute;
  top: -50px;
  right: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-preview-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

.close-preview-btn svg {
  width: 24px;
  height: 24px;
  color: white;
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
  }

  .edit-form-row {
    grid-template-columns: 1fr;
  }

  .user-sidebar {
    position: static;
  }

  .user-card {
    padding: 20px;
  }

  .user-stats {
    flex-direction: row;
    justify-content: space-around;
  }

  .user-menu {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .menu-item {
    flex: 1;
    min-width: 100px;
    justify-content: center;
  }

  .content-card {
    flex-direction: column;
  }

  .card-image {
    width: 100%;
    height: 160px;
  }

  .card-actions {
    flex-direction: row;
    justify-content: flex-end;
  }

  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .filter-tabs {
    width: 100%;
    overflow-x: auto;
  }
}

/* 编辑弹窗 */
@keyframes modalSlide {
  from { opacity: 0; transform: translateY(20px) scale(0.97); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(45, 58, 30, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.2s ease;
}

.edit-modal {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  width: 94%;
  max-width: 860px;
  max-height: 88vh;
  overflow-y: auto;
  box-shadow: 0 24px 80px rgba(45, 58, 30, 0.2);
  animation: modalSlide 0.25s ease;
}

.edit-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid var(--color-card-border);
}

.edit-modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  font-family: var(--font-display);
}

.edit-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: 50%;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-close:hover {
  background: var(--color-card-border);
  color: var(--color-text-primary);
}

.edit-close svg {
  width: 18px;
  height: 18px;
}

.edit-modal-body {
  padding: 24px 28px;
}

.edit-field {
  margin-bottom: 20px;
}

.edit-field label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.edit-field input,
.edit-field textarea {
  width: 100%;
  padding: 12px 16px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-card-border);
  border-radius: 10px;
  font-size: 15px;
  color: var(--color-text-primary);
  outline: none;
  transition: all 0.2s ease;
}

.edit-field input:focus,
.edit-field textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(91,140,62,0.1);
}

.edit-field textarea {
  resize: vertical;
  min-height: 120px;
  font-family: var(--font-body);
}

.edit-field select {
  width: 100%;
  padding: 12px 16px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-card-border);
  border-radius: 10px;
  font-size: 15px;
  color: var(--color-text-primary);
  outline: none;
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%236B8A52' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  padding-right: 40px;
}

.edit-form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.edit-upload-area {
  border: 2px dashed var(--color-card-border);
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.edit-upload-area:hover {
  border-color: var(--color-primary);
  background: var(--color-bg-secondary);
}

.edit-upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: var(--color-text-muted);
}

.edit-upload-placeholder svg {
  width: 32px;
  height: 32px;
}

.edit-upload-placeholder span {
  font-size: 13px;
}

.edit-cover-preview {
  width: 100%;
  max-height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.edit-itinerary-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.edit-itinerary-item {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-card-border);
  border-radius: 10px;
  padding: 14px;
}

.edit-itinerary-item input {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid var(--color-card-border);
  border-radius: 7px;
  font-size: 13px;
  background: var(--color-card-bg);
  color: var(--color-text-primary);
  margin-bottom: 8px;
  outline: none;
}

.edit-itinerary-item input:last-child {
  margin-bottom: 0;
}

.edit-itinerary-item input::placeholder {
  color: var(--color-text-muted);
}

.edit-itinerary-item input:focus {
  border-color: var(--color-primary);
}

.edit-itinerary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.edit-day-badge {
  display: inline-block;
  padding: 4px 12px;
  background: var(--color-primary);
  color: #FFFFFF;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
}

.edit-remove-btn {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(224,112,112,0.12);
  border: none;
  border-radius: 6px;
  color: #E07070;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-remove-btn:hover {
  background: rgba(224,112,112,0.25);
}

.edit-remove-btn svg {
  width: 14px;
  height: 14px;
}

.edit-spots-list {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed var(--color-card-border);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.edit-spot-item {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 7px;
  padding: 10px;
  position: relative;
}

.edit-spot-top {
  display: flex;
  gap: 10px;
}

.edit-spot-img-upload {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border: 2px dashed var(--color-card-border);
  border-radius: 7px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.edit-spot-img-upload:hover {
  border-color: var(--color-primary);
}

.edit-spot-img-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.edit-spot-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  color: var(--color-text-muted);
}

.edit-spot-img-placeholder svg {
  width: 22px;
  height: 22px;
}

.edit-spot-img-placeholder span {
  font-size: 10px;
}

.edit-spot-fields {
  flex: 1;
  min-width: 0;
}

.edit-spot-fields input {
  margin-bottom: 6px;
}

.edit-spot-fields input:last-child {
  margin-bottom: 0;
}

.edit-spot-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.edit-spot-row input {
  margin-bottom: 0;
}

.edit-remove-spot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(224,112,112,0.12);
  border: none;
  border-radius: 4px;
  color: #E07070;
  cursor: pointer;
}

.edit-remove-spot svg {
  width: 12px;
  height: 12px;
}

.edit-add-spot {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 7px;
  margin-top: 8px;
  background: transparent;
  border: 1px dashed var(--color-card-border);
  border-radius: 7px;
  color: var(--color-text-muted);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-add-spot:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.edit-add-day {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  background: transparent;
  border: 2px dashed var(--color-card-border);
  border-radius: 10px;
  color: var(--color-text-muted);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-add-day:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-bg-secondary);
}

.edit-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px;
  border-top: 1px solid var(--color-card-border);
}

.edit-modal-footer .btn-cancel {
  padding: 10px 24px;
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: 10px;
  font-size: 14px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-modal-footer .btn-cancel:hover {
  background: var(--color-card-border);
}

.edit-modal-footer .btn-save {
  padding: 10px 28px;
  background: var(--gradient-secondary);
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #FFFFFF;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-modal-footer .btn-save:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(45,58,30,0.2);
}

/* 粉丝/关注列表弹窗 */
.follow-modal {
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 20px;
  width: 90%;
  max-width: 420px;
  max-height: 70vh;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(45, 58, 30, 0.2);
  animation: modalSlide 0.25s ease;
  display: flex;
  flex-direction: column;
}

.follow-modal .modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-card-border);
}

.follow-modal .modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  font-family: var(--font-display);
  margin: 0;
}

.follow-modal .close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary);
  border: none;
  border-radius: 8px;
  font-size: 20px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.follow-modal .close-btn:hover {
  background: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.follow-modal-body {
  overflow-y: auto;
  flex: 1;
  padding: 8px 0;
}

.follow-modal-body .loading,
.follow-modal-body .empty {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-muted);
  font-size: 14px;
}

.follow-list {
  display: flex;
  flex-direction: column;
}

.follow-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 24px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.follow-item:hover {
  background: var(--color-bg-secondary);
}

.follow-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--color-card-border);
}

.follow-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-primary);
}
</style>
