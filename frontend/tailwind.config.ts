import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        ink: "#080b12",
        panel: "#111827",
        mint: "#63e6be",
      },
      boxShadow: {
        glow: "0 0 60px rgba(99, 230, 190, 0.14)",
      },
    },
  },
  plugins: [],
};

export default config;
