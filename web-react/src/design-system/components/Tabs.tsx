import type { ReactNode } from 'react'
import { Tabs as RadixTabs } from 'radix-ui'
import './Tabs.css'

export type TabItem = {
  content: ReactNode
  label: string
  value: string
}

export type TabsProps = {
  defaultValue: string
  items: TabItem[]
}

export function Tabs({ defaultValue, items }: TabsProps) {
  return (
    <RadixTabs.Root defaultValue={defaultValue}>
      <RadixTabs.List aria-label="Content sections" className="tabs__list">
        {items.map((item) => (
          <RadixTabs.Trigger
            className="tabs__trigger type-tab"
            key={item.value}
            value={item.value}
          >
            {item.label}
          </RadixTabs.Trigger>
        ))}
      </RadixTabs.List>
      {items.map((item) => (
        <RadixTabs.Content key={item.value} value={item.value}>
          {item.content}
        </RadixTabs.Content>
      ))}
    </RadixTabs.Root>
  )
}
