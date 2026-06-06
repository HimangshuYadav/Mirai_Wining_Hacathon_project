"use client";

import {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from "react";

export type WebcamHandle = {
  capture: () => string | null;
};

type WebcamProps = {
  className?: string;
  onReadyChange?: (ready: boolean) => void;
};

const Webcam = forwardRef<WebcamHandle, WebcamProps>(function Webcam(
  { className = "", onReadyChange },
  ref,
) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function startCamera() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: "user", width: { ideal: 1280 } },
          audio: false,
        });
        if (!active) {
          stream.getTracks().forEach((track) => track.stop());
          return;
        }
        streamRef.current = stream;
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
          await videoRef.current.play();
          onReadyChange?.(true);
        }
      } catch {
        setError("Camera access is needed to recognize familiar faces.");
        onReadyChange?.(false);
      }
    }

    void startCamera();
    return () => {
      active = false;
      streamRef.current?.getTracks().forEach((track) => track.stop());
      onReadyChange?.(false);
    };
  }, [onReadyChange]);

  useImperativeHandle(ref, () => ({
    capture() {
      const video = videoRef.current;
      if (!video || video.readyState < HTMLMediaElement.HAVE_CURRENT_DATA) {
        return null;
      }
      const canvas = document.createElement("canvas");
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const context = canvas.getContext("2d");
      if (!context) return null;
      context.drawImage(video, 0, 0);
      return canvas.toDataURL("image/jpeg", 0.86);
    },
  }));

  if (error) {
    return (
      <div className={`grid place-items-center bg-panel p-8 text-center ${className}`}>
        <p className="max-w-sm text-slate-300">{error}</p>
      </div>
    );
  }

  return (
    <video
      ref={videoRef}
      muted
      playsInline
      className={`h-full w-full object-cover [transform:scaleX(-1)] ${className}`}
      aria-label="Live camera feed"
    />
  );
});

export default Webcam;
