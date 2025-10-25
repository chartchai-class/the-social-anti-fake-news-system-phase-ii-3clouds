<template>
  <div class="bg-gray-100 p-6 rounded-xl mb-8">
    <div class="mb-4">
      <h3 class="text-lg font-bold text-gray-800 mb-2">
        Vote Summary :
        <span
          :class="{
            'bg-red-600': voteSummary.fake > voteSummary.real,
            'bg-green-600': voteSummary.real > voteSummary.fake,
            'bg-gray-500': voteSummary.real === voteSummary.fake,
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
</template>

<script setup lang="ts">
import { computed, defineProps } from 'vue';
import type { VoteSummary } from '../stores/news';

const props = defineProps<{
  voteSummary: VoteSummary;
}>();

const fakeVotes = computed(() => props.voteSummary.fake || 0);
const realVotes = computed(() => props.voteSummary.real || 0);
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
</script>