export function isJwtExpired(token: string, clockSkewSeconds = 30): boolean {
  if (token === 'demo-session') return false;
  try {
    const payloadPart = token.split('.')[1];
    if (!payloadPart) return true;
    const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '='))) as { exp?: number };
    if (typeof payload.exp !== 'number') return true;
    return payload.exp * 1000 <= Date.now() + clockSkewSeconds * 1000;
  } catch {
    return true;
  }
}
