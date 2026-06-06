"use client";

import Link from "next/link";
import { FormEvent, useRef, useState } from "react";
import Webcam, { WebcamHandle } from "@/components/Webcam";
import { enrollPerson } from "@/lib/api";

export default function EnrollPage() {
  const webcamRef = useRef<WebcamHandle>(null);
  const [name, setName] = useState("");
  const [relationship, setRelationship] = useState("");
  const [capturedImage, setCapturedImage] = useState("");
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  function capture() {
    const image = webcamRef.current?.capture();
    if (image) {
      setCapturedImage(image);
      setMessage("Photo captured. Submit when ready.");
    }
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    if (!capturedImage) {
      setMessage("Capture a clear photo before submitting.");
      return;
    }

    setSubmitting(true);
    setMessage("");
    try {
      const person = await enrollPerson({ name, relationship, image: capturedImage });
      setMessage(`${person.name} has been added as ${person.relationship}.`);
      setName("");
      setRelationship("");
      setCapturedImage("");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Enrollment failed.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="mx-auto min-h-screen max-w-6xl px-5 pb-12 pt-24">
      <div className="mb-7 flex items-end justify-between gap-4">
        <div>
          <p className="mb-2 text-sm font-medium uppercase tracking-[0.22em] text-mint">
            New person
          </p>
          <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">
            Add a familiar face
          </h1>
        </div>
        <Link href="/" className="text-sm text-slate-400 hover:text-white">
          Back to camera
        </Link>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.25fr_0.75fr]">
        <section className="relative min-h-[460px] overflow-hidden rounded-[2rem] border border-white/10 bg-panel shadow-glow">
          {capturedImage ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={capturedImage}
              alt="Captured enrollment"
              className="absolute inset-0 h-full w-full object-cover [transform:scaleX(-1)]"
            />
          ) : (
            <Webcam ref={webcamRef} className="absolute inset-0" />
          )}
          <div className="absolute inset-x-0 bottom-0 flex justify-center bg-gradient-to-t from-black/80 to-transparent px-5 pb-6 pt-20">
            <button
              type="button"
              onClick={capturedImage ? () => setCapturedImage("") : capture}
              className="rounded-full bg-white px-6 py-3 font-medium text-slate-950 transition hover:bg-mint"
            >
              {capturedImage ? "Retake photo" : "Capture photo"}
            </button>
          </div>
        </section>

        <form
          onSubmit={submit}
          className="flex flex-col rounded-[2rem] border border-white/10 bg-panel/70 p-6 sm:p-8"
        >
          <label className="mb-6 block">
            <span className="mb-2 block text-sm font-medium text-slate-300">
              Name
            </span>
            <input
              required
              maxLength={100}
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Jake"
              className="w-full rounded-2xl border border-white/10 bg-black/25 px-4 py-3 outline-none transition placeholder:text-slate-600 focus:border-mint/70"
            />
          </label>

          <label className="mb-6 block">
            <span className="mb-2 block text-sm font-medium text-slate-300">
              Relationship
            </span>
            <input
              required
              maxLength={100}
              value={relationship}
              onChange={(event) => setRelationship(event.target.value)}
              placeholder="Son"
              className="w-full rounded-2xl border border-white/10 bg-black/25 px-4 py-3 outline-none transition placeholder:text-slate-600 focus:border-mint/70"
            />
          </label>

          <p className="mb-6 min-h-12 text-sm leading-relaxed text-slate-400">
            {message ||
              "Use a well-lit, front-facing photo with only one person visible."}
          </p>

          <button
            type="submit"
            disabled={submitting}
            className="mt-auto rounded-2xl bg-mint px-5 py-3 font-semibold text-slate-950 transition hover:bg-emerald-300 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {submitting ? "Adding person..." : "Add familiar face"}
          </button>
        </form>
      </div>
    </main>
  );
}
