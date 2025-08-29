// Validação simples de CPF: 11 dígitos e não todos iguais.
// (O backend deve validar oficialmente; aqui é apenas pré-validação).
export function sanitizeCPF(v) {
  return (v || '').replace(/\D/g, '')
}

export function isValidCPF(v) {
  const d = sanitizeCPF(v)
  if (d.length !== 11) return false
  if (/^(\d)\1{10}$/.test(d)) return false
  return true
}

export function maskCPF(v) {
  const d = sanitizeCPF(v).slice(0, 11)
  const parts = []
  if (d.length > 3) parts.push(d.slice(0,3))
  if (d.length > 6) parts.push(d.slice(3,6))
  if (d.length > 9) parts.push(d.slice(6,9))
  let rest = d.slice(9)
  let masked = ''
  if (parts.length === 0) masked = d
  if (parts.length === 1) masked = parts[0] + '.' + d.slice(3)
  if (parts.length === 2) masked = parts[0] + '.' + parts[1] + '.' + d.slice(6)
  if (parts.length === 3) masked = parts[0] + '.' + parts[1] + '.' + parts[2] + '-' + rest
  return masked || d
}
