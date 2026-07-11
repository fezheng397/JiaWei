import type { ReactNode } from 'react'
import { DropdownMenu } from 'radix-ui'
import { Button } from './Button'
import './Dropdown.css'

export type DropdownItem = {
  disabled?: boolean
  label: ReactNode
  onSelect?: () => void
  value: string
}

export type DropdownProps = {
  align?: 'start' | 'center' | 'end'
  items: DropdownItem[]
  label?: ReactNode
  trigger: ReactNode
}

export function Dropdown({ align = 'end', items, label, trigger }: DropdownProps) {
  return (
    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild>
        <Button className="dropdown__trigger" type="button" variant="ghost">
          {trigger}
        </Button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Portal>
        <DropdownMenu.Content align={align} className="dropdown__content" sideOffset={8}>
          {label ? (
            <>
              <DropdownMenu.Label className="dropdown__label type-meta-label">
                {label}
              </DropdownMenu.Label>
              <DropdownMenu.Separator className="dropdown__separator" />
            </>
          ) : null}
          {items.map((item) => (
            <DropdownMenu.Item
              className="dropdown__item"
              disabled={item.disabled}
              key={item.value}
              onSelect={item.onSelect}
            >
              {item.label}
            </DropdownMenu.Item>
          ))}
        </DropdownMenu.Content>
      </DropdownMenu.Portal>
    </DropdownMenu.Root>
  )
}
