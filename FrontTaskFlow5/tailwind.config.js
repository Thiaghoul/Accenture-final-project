/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ["class"],
  content: [
    // Seus caminhos de conteúdo (mantidos do seu arquivo)
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
    "*.{js,ts,jsx,tsx,mdx}",
    './pages/**/*.{ts,tsx}',
    './components/**/*.{ts,tsx}',
    './app/**/*.{ts,tsx}',
  ],
  prefix: "",
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1400px",
      },
    },
    extend: {
      // SUAS CORES PERSONALIZADAS (mantidas do seu arquivo)
      colors: {
        primary: {
          DEFAULT: "#4A90E2",
          hover: "#357ABD",
          light: "#E3F2FD",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))", // Mantém o padrão shadcn
          foreground: "hsl(var(--secondary-foreground))", // Mantém o padrão shadcn
          hover: "#3BC9A8", // Sua customização
        },
        accent: {
          DEFAULT: "hsl(var(--accent))", // Mantém o padrão shadcn
          foreground: "hsl(var(--accent-foreground))", // Mantém o padrão shadcn
          hover: "#E09612", // Sua customização
        },
        success: "#7ED321",
        warning: "#F8E71C",
        error: "#D0021B",
        neutral: {
          50: "#FAFAFA",
          100: "#F5F5F5",
          200: "#E5E5E5",
          300: "#D4D4D4",
          400: "#A3A3A3",
          500: "#737373",
          600: "#525252",
          700: "#404040",
          800: "#262626",
          900: "#171717",
        },
        // CORES PADRÃO DO SHADCN (necessárias para os componentes)
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
      },
      // SUAS FONTES PERSONALIZADAS (mantidas do seu arquivo)
      fontFamily: {
        sans: ["Roboto", "system-ui", "sans-serif"],
        secondary: ["Open Sans", "system-ui", "sans-serif"],
        mono: ["Fira Code", "monospace"],
      },
      // SEU BORDER-RADIUS PERSONALIZADO (mantido do seu arquivo)
      borderRadius: {
        lg: "8px",
        md: "6px",
        sm: "4px",
      },
      // ANIMAÇÕES PADRÃO DO SHADCN (necessárias para os componentes)
      keyframes: {
        "accordion-down": {
          from: { height: "0" },
          to: { height: "var(--radix-accordion-content-height)" },
        },
        "accordion-up": {
          from: { height: "var(--radix-accordion-content-height)" },
          to: { height: "0" },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
      },
    },
  },
  plugins: [
    // A forma correta de adicionar o plugin
    require("tailwindcss-animate")
  ],
}