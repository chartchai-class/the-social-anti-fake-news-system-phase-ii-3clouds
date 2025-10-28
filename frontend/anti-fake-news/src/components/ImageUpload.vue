<script setup lang="ts">
import Uploader from 'vue-media-upload'
import { computed, ref, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const authorizeHeader = computed(() => {
    return { authorization: authStore.authorizationHeader }
})
interface Props {
    modelValue?: string[]
    maxFiles?: number
    fieldName?: string
}

const props = withDefaults(defineProps<Props>(), {
    modelValue: () => [],
    maxFiles: 10,
    fieldName: 'image'
})

const convertStringToMedia = (str: string[]): any => {
    if (!Array.isArray(str)) {
        return []
    }
    return str.map((element: string) => {
        return {
            name: element
        }
    })
}

const emit = defineEmits(['update:modelValue'])

const convertMediaToString = (media: any[]): string[] => {
    const output: string[] = []
    if (Array.isArray(media)) {
        media.forEach((element: any) => {
            output.push(element.name)
        })
    }
    return output
}

const media = ref(convertStringToMedia(props.modelValue || []))
const uploadUrl = ref(import.meta.env.VITE_UPLOAD_URL)

watch(() => props.modelValue, (newVal) => {
    media.value = convertStringToMedia(newVal || [])
}, { deep: true })

const onChanged = (files: any) => {
    let resultFiles = files

    if (files.length > props.maxFiles) {
        resultFiles = files.slice(-props.maxFiles)
        setTimeout(() => {
            media.value = resultFiles
        }, 0)
    }

    emit('update:modelValue', convertMediaToString(resultFiles))
}
</script>

<template>
    <div class="w-full">
        <!-- Header with file count -->
        <div class="flex items-center justify-between mb-4">
            <div class="text-sm font-semibold text-gray-800">
                Images
                <span class="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {{ media.length }}/{{ props.maxFiles }}
                </span>
            </div>
            <div v-if="media.length >= props.maxFiles" class="flex items-center gap-1.5 text-xs text-amber-600 font-medium">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                </svg>
                Maximum reached
            </div>
        </div>

        <!-- Uploader Container -->
        <Uploader 
            :server="uploadUrl" 
            @change="onChanged" 
            :media="media" 
            :headers="authorizeHeader"
            :max-files="props.maxFiles" 
            :field-name="props.fieldName"
            :class="{ 'max-reached': media.length >= props.maxFiles }"
            class="custom-uploader"
        />

        <!-- Helper Text -->
        <div class="mt-3">
            <p v-if="props.maxFiles === 1 && media.length >= 1" class="text-xs text-gray-600 flex items-center gap-2">
                <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>Delete the current image to upload a new one</span>
            </p>
            <p v-else-if="media.length < props.maxFiles" class="text-xs text-gray-600 flex items-center gap-2">
                <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <span>Click to upload (max {{ props.maxFiles }} files)</span>
            </p>
        </div>
    </div>
</template>

<style scoped>
/* ซ่อนปุ่ม + เมื่อถึงจำนวนสูงสุด */
.max-reached :deep(.uploader-btn),
.max-reached :deep(label),
.max-reached :deep(.add-button),
.max-reached :deep([class*="add"]):not([class*="added"]) {
    display: none !important;
}

/* ปรับแต่ง uploader ให้สวยงาม */
.custom-uploader :deep(.uploader-container) {
    gap: 16px;
}

.custom-uploader :deep(.uploader-item) {
    border-radius: 12px;
    overflow: hidden;
    border: 2px solid #e5e7eb;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.custom-uploader :deep(.uploader-btn) {
    border-radius: 12px;
    border: 2px dashed #d1d5db;
    background: linear-gradient(to bottom, #ffffff, #f9fafb);
    min-height: 120px;
}

.custom-uploader :deep(.delete-btn) {
    background: rgba(239, 68, 68, 0.95);
    backdrop-filter: blur(8px);
    border-radius: 8px;
    transition: all 0.2s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.custom-uploader :deep(.delete-btn):hover {
    background: rgba(220, 38, 38, 1);
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}
</style>