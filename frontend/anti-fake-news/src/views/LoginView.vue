<script setup lang="ts">
import { ref } from 'vue';
import InputText from '@/components/InputText.vue';
import * as yup from 'yup';
import { useField, useForm } from 'vee-validate';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useMessageStore } from '@/stores/message';

// --- Validation Schema ---
// (ใช้ Username แทน Email)
const validationSchema = yup.object({
    username: yup.string().required('Username is required'),
    password: yup.string().required('Password is required')
});

// --- Form Handling (VeeValidate) ---
const { errors, handleSubmit } = useForm({
    validationSchema,
    initialValues: {
        username: "",
        password: ""
    }
});

// --- Form Fields ---
const { value: username } = useField<string>('username');
const { value: password } = useField<string>('password');

// --- Stores and Router ---
const authStore = useAuthStore();
const router = useRouter();
const messageStore = useMessageStore(); // (Store สำหรับแสดง Error)
const isSubmitting = ref(false); // (State สำหรับ Loading ตอนกด Login)

// --- Submit Handler ---
const onSubmit = handleSubmit(async (values) => {
    isSubmitting.value = true;
    messageStore.resetMessage(); // เคลียร์ข้อความเก่า (ถ้ามี)

    try {
        // เรียก login action จาก authStore
        await authStore.login(values.username, values.password);

        // แสดงข้อความ Success ชั่วครู่ (Optional)
        messageStore.updateMessage('Login successful!', 'success');
        setTimeout(() => messageStore.resetMessage(), 2000);

        // Redirect ไปหน้าแรก (News List)
        await router.push({ name: 'home' }); // (ตรวจสอบว่า Route ชื่อ 'home' หรือ '/')

    } catch (err: any) {
        // แสดง Error จาก Backend (ถ้ามี) หรือข้อความทั่วไป
        const errorMessage = err.response?.data?.message || 'Login failed. Invalid username or password.';
        messageStore.updateMessage(errorMessage, 'error');

        // ตั้งเวลาลบข้อความ Error
        setTimeout(() => {
            messageStore.resetMessage();
        }, 4000);
        console.error('Login error:', err);

    } finally {
        isSubmitting.value = false;
    }
});
</script>

<template>
    <div class="flex min-h-full flex-1 flex-col justify-center py-12 sm:px-6 lg:px-8 bg-gray-50">
        <div v-if="messageStore.message" class="fixed top-5 right-5 z-50 p-4 rounded-md shadow-lg"
            :class="messageStore.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'">
            {{ messageStore.message }}
        </div>

        <div class="sm:mx-auto sm:w-full sm:max-w-md">
            <img class="mx-auto h-16 w-auto" src="/logo.png" alt="3Clouds News Logo" />
        </div>

        <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
            <div class="bg-white px-4 py-8 shadow-lg sm:rounded-lg sm:px-10">

                <h2 class="text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                    Sign in to 3Clouds News
                </h2>

                <form class="mt-8 space-y-6" @submit.prevent="onSubmit" novalidate>
                    <div>
                        <label for="username" class="block text-sm font-medium text-left leading-6 text-gray-900">
                            Username
                        </label>
                        <div class="mt-2">
                            <InputText id="username" type="text" v-model="username" placeholder="Your Username"
                                :error="errors['username']" />
                        </div>
                    </div>

                    <div>
                        <div class="flex items-center justify-between">
                            <label for="password" class="block text-sm font-medium leading-6 text-gray-900">
                                Password
                            </label>
                            <div class="text-sm">
                                <a href="#" class="font-semibold text-blue-600 hover:text-blue-500">Forgot password?</a>
                            </div>
                        </div>
                        <div class="mt-2">
                            <InputText id="password" v-model="password" type="password" placeholder="Your Password"
                                :error="errors['password']" />
                        </div>
                    </div>

                    <div>
                        <button type="submit" :disabled="isSubmitting"
                            class="flex w-full justify-center rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:opacity-50 transition-colors duration-300">
                            <span v-if="isSubmitting"
                                class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></span>
                            Sign in
                        </button>
                    </div>
                </form>

                <div class="mt-6">
                    <div class="relative">
                        <div class="absolute inset-0 flex items-center">
                            <div class="w-full border-t border-gray-300" />
                        </div>
                        <div class="relative flex justify-center text-sm">
                            <span class="bg-white px-2 text-gray-500">Or</span>
                        </div>
                    </div>
                </div>

                <p class="mt-6 text-center text-sm text-gray-500">
                    Not a member yet?
                    {{ ' ' }}
                    <RouterLink :to="{ name: 'register' }"
                        class="font-semibold leading-6 text-blue-600 hover:text-blue-500">
                        Register here
                    </RouterLink>
                </p>

            </div>
        </div>
    </div>
</template>
