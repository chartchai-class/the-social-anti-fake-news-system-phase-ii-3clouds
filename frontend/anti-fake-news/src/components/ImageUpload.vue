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
    // Add safety check to ensure str is an array
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

    // บังคับจำกัดจำนวนไฟล์
    if (files.length > props.maxFiles) {
        // เก็บเฉพาะรูปล่าสุดตามจำนวน maxFiles
        resultFiles = files.slice(-props.maxFiles)

        // อัพเดท media ทันทีเพื่อให้ UI แสดงผลถูกต้อง
        setTimeout(() => {
            media.value = resultFiles
        }, 0)
    }

    // ส่งค่ากลับไปยัง parent
    emit('update:modelValue', convertMediaToString(resultFiles))
}
</script>

<template>
    <div class="image-upload-container">
        <Uploader :server="uploadUrl" @change="onChanged" :media="media" :headers="authorizeHeader"
            :max-files="props.maxFiles" :field-name="props.fieldName"
            :class="{ 'max-reached': media.length >= props.maxFiles }"></Uploader>
        <p v-if="props.maxFiles === 1 && media.length >= 1" class="warning-text">
            ⚠️ You can only upload 1 image. Please delete the current image before adding a new one.
        </p>
    </div>
</template>

<style scoped>
.image-upload-container {
    /* max-width: 400px; */
    /* margin: 0 auto; */
}

.warning-text {
    color: #f59e0b;
    font-size: 13px;
    margin-top: 8px;
    text-align: center;
}

/* ซ่อนปุ่ม + เมื่อถึงจำนวนสูงสุด */
.max-reached :deep(.uploader-btn),
.max-reached :deep(label),
.max-reached :deep(.add-button),
.max-reached :deep([class*="add"]):not([class*="added"]) {
    display: none !important;
}
</style>