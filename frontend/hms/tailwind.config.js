/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,ts,jsx,tsx}"], // <== VERY IMPORTANT
  theme: {
    extend: {
        animation: {
        'spin-slow': 'spin 8s linear infinite',
      },
      colors: {
        fontFamily:{
     body: 'poppins, sanserif',
     sans: 'poppins, sanserif',
     heading: 'Meriweather,serif'
        },
        colors:{
       'dark': '#212529',
        'light': '#f8f9fa',
        },
        primary: {
        '50': '#f1fcfa',
        '100': '#cff8ef',
        '200': '#9ff0e1',
        '300': '#67e1cf',
        '400': '#32b9a9',
        '500': '#1fad9f',
        '600': '#168b82',
        '700': '#166f69',
        '800': '#165955',
        '900': '#174a47',
        '950': '#072c2b',
    },
       'neutral': {
         '50': '#f6f6f6',
        '100': '#e7e7e7',
        '200': '#d1d1d1',
        '300': '#b0b0b0',
        '400': '#888888',
        '500': '#6d6d6d',
        '600': '#5d5d5d',
        '700': '#4f4f4f',
        '800': '#454545',
        '900': '#3d3d3d',
        '950': '#000000',

        },

'dark': "#212529",
'light': "#F0F3FB"

      }
    },
  },
  plugins: [],
};
