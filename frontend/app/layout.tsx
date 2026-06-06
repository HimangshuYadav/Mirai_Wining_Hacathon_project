import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "Remember Me",
  description: "A gentle memory assistant for familiar faces.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <header className="fixed inset-x-0 top-0 z-50 border-b border-white/10 bg-ink/80 backdrop-blur-xl">
          <nav className="mx-auto flex h-16 max-w-6xl items-center justify-between px-5">
            <Link href="/" className="text-lg font-semibold tracking-tight">
              Remember<span className="text-mint">Me</span>
            </Link>
            <Link
              href="/enroll"
              className="rounded-full border border-white/15 px-4 py-2 text-sm text-slate-200 transition hover:border-mint/60 hover:text-mint"
            >
              Add a familiar face
            </Link>
          </nav>
        </header>
        {children}
      </body>
    </html>
  );
}
