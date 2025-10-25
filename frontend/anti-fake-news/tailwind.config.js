/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      fontFamily: {
        'alfa-slab': ['"Alfa Slab One"', 'serif'],
      },
      colors: {
        'my-blue': '#1C325B',
      }
    },
  },
  plugins: [],
}