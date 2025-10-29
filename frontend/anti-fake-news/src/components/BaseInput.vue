<template>
  <div class="mb-6">
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      type="text"
      placeholder="Search by topic, detail, or reporter..."
      class="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';

defineProps({
  modelValue: String
});

defineEmits(['update:modelValue']);
</script>