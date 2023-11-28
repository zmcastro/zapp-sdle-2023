/** @type {import('tailwindcss').Config}*/
const config = {
    content: ["./src/**/*.{html,js,svelte,ts}"],

    theme: {
        extend: {},
    },

    plugins: [require("daisyui")],

    daisyui: {
        themes: ["autumn"],
    },
};

module.exports = config;
