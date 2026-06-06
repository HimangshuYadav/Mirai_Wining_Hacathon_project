export const API_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8000";

export type PersonMatch = {
  person_id: string;
  name: string;
  relationship: string;
};

export type IdentifyResponse = {
  match: PersonMatch | null;
  confidence: number;
};

export type SummaryResponse = PersonMatch & {
  summary: string;
};

async function post<T>(path: string, body: unknown): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!response.ok) {
    const error = await response.json().catch(() => null);
    throw new Error(error?.detail ?? "The request could not be completed.");
  }
  return response.json() as Promise<T>;
}

export function identifyFace(image: string) {
  return post<IdentifyResponse>("/identify", { image });
}

export function summarizePerson(personId: string) {
  return post<SummaryResponse>("/summarize", { person_id: personId });
}

export function enrollPerson(input: {
  name: string;
  relationship: string;
  image: string;
}) {
  return post<PersonMatch>("/enroll", input);
}

export function logMemory(personId: string, note: string) {
  return post<{ person_id: string; note: string; created_at: string }>(
    "/memory/log",
    { person_id: personId, note },
  );
}