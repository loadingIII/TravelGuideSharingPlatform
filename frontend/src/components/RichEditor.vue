<template>
  <div class="rich-editor" :class="{ 'is-focused': isFocused }">
    <div class="editor-toolbar" v-if="editor">
      <button class="toolbar-btn" :class="{ active: editor.isActive('heading', { level: 2 }) }" @click="editor.chain().focus().toggleHeading({ level: 2 }).run()" title="标题">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4v16M20 4v16M4 12h16"/><path d="M8 4v16M16 4v16"/></svg>
      </button>
      <button class="toolbar-btn" :class="{ active: editor.isActive('heading', { level: 3 }) }" @click="editor.chain().focus().toggleHeading({ level: 3 }).run()" title="小标题">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4v16M20 4v16M4 12h16"/><path d="M8 4v16M16 4v16" opacity="0.5"/><path d="M14 16h6M17 13v6" opacity="0.5"/></svg>
      </button>

      <div class="toolbar-divider"></div>

      <button class="toolbar-btn" :class="{ active: editor.isActive('bold') }" @click="editor.chain().focus().toggleBold().run()" title="加粗">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 4h8a4 4 0 014 4 4 4 0 01-4 4H6zM6 12h9a4 4 0 014 4 4 4 0 01-4 4H6z"/></svg>
      </button>
      <button class="toolbar-btn" :class="{ active: editor.isActive('italic') }" @click="editor.chain().focus().toggleItalic().run()" title="斜体">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 4h-9M14 20H5M15 4L9 20"/></svg>
      </button>

      <div class="toolbar-divider"></div>

      <button class="toolbar-btn" :class="{ active: editor.isActive('bulletList') }" @click="editor.chain().focus().toggleBulletList().run()" title="无序列表">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01"/></svg>
      </button>
      <button class="toolbar-btn" :class="{ active: editor.isActive('orderedList') }" @click="editor.chain().focus().toggleOrderedList().run()" title="有序列表">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 6h11M10 12h11M10 18h11"/><path d="M4 6h1v4M4 10h2M6 18H4c0-1 2-2 2-3s-1-1.5-2-1"/></svg>
      </button>

      <div class="toolbar-divider"></div>

      <button class="toolbar-btn" :class="{ active: editor.isActive('blockquote') }" @click="editor.chain().focus().toggleBlockquote().run()" title="引用">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 21c3 0 7-1 7-8V5c0-1.25-.756-2.017-2-2H4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2 1 0 1 0 1 1v1c0 1-1 2-2 2s-1 .008-1 1.031V20c0 1 0 1 1 1zM15 21c3 0 7-1 7-8V5c0-1.25-.757-2.017-2-2h-4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2h.75c0 2.25.25 4-2.75 4v3c0 1 0 1 1 1z"/></svg>
      </button>
      <button class="toolbar-btn" @click="addImage" title="插入图片">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/></svg>
      </button>
    </div>

    <editor-content :editor="editor" class="editor-content" />
  </div>
</template>

<script setup>
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Placeholder from '@tiptap/extension-placeholder'
import { watch, ref } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '开始写作...' }
})

const emit = defineEmits(['update:modelValue'])
const isFocused = ref(false)

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit.configure({
      heading: { levels: [2, 3] }
    }),
    Image,
    Placeholder.configure({ placeholder: props.placeholder })
  ],
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML())
  },
  onFocus: () => { isFocused.value = true },
  onBlur: () => { isFocused.value = false },
  editorProps: {
    attributes: {
      class: 'prose-content'
    }
  }
})

const addImage = () => {
  const url = prompt('输入图片URL')
  if (url) {
    editor.value?.chain().focus().setImage({ src: url }).run()
  }
}

watch(() => props.modelValue, (val) => {
  if (editor.value && val !== editor.value.getHTML()) {
    editor.value.commands.setContent(val, false)
  }
})
</script>

<style>
.rich-editor {
  border: 1px solid var(--color-card-border);
  border-radius: 12px;
  overflow: hidden;
  background: var(--color-bg-secondary);
  transition: all 0.3s ease;
}

.rich-editor.is-focused {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(91,140,62,0.1);
}

.editor-toolbar {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 8px 10px;
  background: var(--color-card-bg);
  border-bottom: 1px solid var(--color-card-border);
  flex-wrap: wrap;
}

.toolbar-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.toolbar-btn svg {
  width: 18px;
  height: 18px;
}

.toolbar-btn:hover {
  background: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.toolbar-btn.active {
  background: var(--color-primary);
  color: #FFFFFF;
}

.toolbar-divider {
  width: 1px;
  height: 22px;
  background: var(--color-card-border);
  margin: 0 4px;
}

.editor-content {
  padding: 16px 20px;
  min-height: 200px;
}

.editor-content .ProseMirror {
  outline: none;
  min-height: 200px;
  color: var(--color-text-primary);
  font-size: 15px;
  line-height: 1.75;
}

.editor-content .ProseMirror p {
  margin: 0 0 8px;
}

.editor-content .ProseMirror p.is-editor-empty:first-child::before {
  content: attr(data-placeholder);
  color: var(--color-text-muted);
  pointer-events: none;
  float: left;
  height: 0;
}

.editor-content .ProseMirror h2 {
  font-size: 22px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 24px 0 12px;
  font-family: var(--font-display);
}

.editor-content .ProseMirror h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 20px 0 10px;
  font-family: var(--font-display);
}

.editor-content .ProseMirror strong {
  color: var(--color-text-primary);
  font-weight: 600;
}

.editor-content .ProseMirror em {
  color: var(--color-text-secondary);
}

.editor-content .ProseMirror ul,
.editor-content .ProseMirror ol {
  padding-left: 24px;
  margin: 8px 0;
  color: var(--color-text-secondary);
}

.editor-content .ProseMirror li {
  margin-bottom: 4px;
}

.editor-content .ProseMirror ul li {
  list-style: disc;
}

.editor-content .ProseMirror ol li {
  list-style: decimal;
}

.editor-content .ProseMirror blockquote {
  border-left: 3px solid var(--color-primary);
  padding: 8px 16px;
  margin: 12px 0;
  color: var(--color-text-muted);
  font-style: italic;
  background: var(--color-bg-secondary);
  border-radius: 0 8px 8px 0;
}

.editor-content .ProseMirror img {
  max-width: 100%;
  border-radius: 8px;
  margin: 16px 0;
}

.editor-content .ProseMirror img.ProseMirror-selectednode {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.ProseMirror-gapcursor {
  display: none;
}
</style>
