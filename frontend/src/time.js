export function formatDate(ms) {
  const d = new Date(ms)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

/** "1岁3个月" style age at the given time; null when the birthday is unknown or later. */
export function ageAt(birthday, ms) {
  if (!birthday) return null
  const [by, bm, bd] = birthday.split('-').map(Number)
  const d = new Date(ms)
  let months = (d.getFullYear() - by) * 12 + (d.getMonth() + 1 - bm)
  if (d.getDate() < bd) months -= 1
  if (months < 0) return null
  if (months === 0) {
    const days = Math.floor((d - new Date(by, bm - 1, bd)) / 86400000)
    return days <= 0 ? '出生' : `${days}天`
  }
  const y = Math.floor(months / 12)
  const m = months % 12
  if (y === 0) return `${m}个月`
  return m === 0 ? `${y}岁` : `${y}岁${m}个月`
}

/** Groups media (already sorted newest first) by calendar month. */
export function groupByMonth(items) {
  const groups = []
  for (const item of items) {
    const d = new Date(item.time)
    const key = `${d.getFullYear()}-${d.getMonth()}`
    let g = groups[groups.length - 1]
    if (!g || g.key !== key) {
      g = { key, label: `${d.getFullYear()}年${d.getMonth() + 1}月`, time: item.time, items: [] }
      groups.push(g)
    }
    g.items.push(item)
  }
  return groups
}
