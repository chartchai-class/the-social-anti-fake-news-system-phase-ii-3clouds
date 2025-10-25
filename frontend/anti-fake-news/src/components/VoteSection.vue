<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div class="bg-gray-100 p-6 rounded-xl">
            <div class="mb-6">
                <label class="block text-lg font-semibold text-gray-800 mb-4">vote:</label>
                <div class="flex space-x-4">
                    <button
                        @click="selectedVote = 'fake'"
                        :class="{
                            'bg-red-500 text-white': selectedVote === 'fake',
                            'bg-red-100 text-red-600 hover:bg-red-200': selectedVote !== 'fake'
                        }"
                        class="px-6 py-2 rounded-full font-bold transition-colors duration-200"
                    >
                        FAKE ✕
                    </button>
                    <button
                        @click="selectedVote = 'real'"
                        :class="{
                            'bg-green-500 text-white': selectedVote === 'real',
                            'bg-green-100 text-green-600 hover:bg-green-200': selectedVote !== 'real'
                        }"
                        class="px-6 py-2 rounded-full font-bold transition-colors duration-200"
                    >
                        REAL ✓
                    </button>
                </div>
            </div>

            <div class="mb-4">
                <input
                    v-model="voterName"
                    type="text"
                    placeholder="enter your name (optional)"
                    class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
            </div>

            <div class="mb-4">
                <textarea
                    v-model="voterComment"
                    placeholder="write an comment... (optional)"
                    rows="4"
                    class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                ></textarea>
            </div>

            <div class="mb-6">
                <input
                    v-model="voterImage"
                    type="text"
                    placeholder="enter an image url (optional)"
                    class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
            </div>

            <button
                @click="submitVote"
                :disabled="!selectedVote"
                class="w-full bg-gray-800 text-white font-bold py-3 rounded-lg hover:bg-gray-900 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors duration-200"
            >
                Submit
            </button>
        </div>

        <div>
            <div class="bg-white p-6 rounded-xl border">
                <div class="mb-4">
                    <h3 class="text-lg font-bold text-gray-800 mb-2">
                        Vote Summary : 
                        <span
                            :class="{
                                'bg-red-600': fakeVotes > realVotes,
                                'bg-green-600': realVotes > fakeVotes,
                                'bg-gray-500': realVotes === fakeVotes
                            }"
                            class="text-white font-bold px-3 py-1 rounded text-sm uppercase ml-2"
                        >
                            {{ fakeVotes > realVotes ? 'FAKE' : realVotes > fakeVotes ? 'REAL' : 'EQUAL' }}
                        </span>
                    </h3>
                </div>

                <div class="mb-4">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-sm font-medium text-gray-700">Real</span>
                        <span class="text-sm text-gray-600">{{ realPercentage }}% ({{ realVotes }} votes)</span>
                    </div>
                    <div class="w-full bg-gray-200 rounded-full h-4">
                        <div 
                            class="bg-green-500 h-4 rounded-full transition-all duration-300"
                            :style="{ width: `${realPercentage}%` }"
                        ></div>
                    </div>
                </div>

                <div class="mb-4">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-sm font-medium text-gray-700">Fake</span>
                        <span class="text-sm text-gray-600">{{ fakePercentage }}% ({{ fakeVotes }} votes)</span>
                    </div>
                    <div class="w-full bg-gray-200 rounded-full h-4">
                        <div 
                            class="bg-red-500 h-4 rounded-full transition-all duration-300"
                            :style="{ width: `${fakePercentage}%` }"
                        ></div>
                    </div>
                </div>

                <div class="text-right border-t pt-3 mt-4">
                    <span class="text-sm font-semibold text-gray-800">Total: {{ totalVotes }} Votes</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps, defineEmits } from 'vue';
import type { News } from '../stores/news';

const props = defineProps<{
    news: News
}>();

const emit = defineEmits(['submitVote']);

// Vote form data
const selectedVote = ref<'fake' | 'real' | null>(null);
const voterName = ref<string>('');
const voterComment = ref<string>('');
const voterImage = ref<string>('');

const fakeVotes = computed(() => props.news.voteSummary?.fake || 0);
const realVotes = computed(() => props.news.voteSummary?.real || 0);
const totalVotes = computed(() => fakeVotes.value + realVotes.value);

const realPercentage = computed(() => {
    const total = totalVotes.value;
    if (total === 0) return 0;
    return Math.round((realVotes.value / total) * 100);
});

const fakePercentage = computed(() => {
    const total = totalVotes.value;
    if (total === 0) return 0;
    return Math.round((fakeVotes.value / total) * 100);
});

const submitVote = () => {
    if (!selectedVote.value) {
        alert('Please select your vote (Real or Fake)');
        return;
    }
    
    emit('submitVote', {
        userName: voterName.value,
        vote: selectedVote.value,
        commentText: voterComment.value,
        imageUrl: voterImage.value
    });

    // Reset form
    selectedVote.value = null;
    voterName.value = '';
    voterComment.value = '';
    voterImage.value = '';
};
</script>