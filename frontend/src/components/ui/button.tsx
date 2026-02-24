import { Button as RadixButton } from "@radix-ui/themes";
import type { ReactNode, CSSProperties } from "react";

type ButtonProps = {
  children: ReactNode;
  onClick?: () => void;
  variant?: "solid" | "soft" | "outline" | "ghost";
  color?: "gray" | "blue" | "green" | "red";
  size?: "1" | "2" | "3";
  className?: string;
  style?: CSSProperties;
  type?: "button" | "submit" | "reset";
  disabled?: boolean;
};

export function Button({
  children,
  onClick,
  variant = "solid",
  color,
  size = "3",
  className,
  style,
  type = "button",
  disabled = false,
}: ButtonProps) {
  return (
    <RadixButton
      onClick={onClick}
      variant={variant}
      color={color}
      size={size}
      className={className}
      style={style}
      type={type}
      disabled={disabled}
    >
      {children}
    </RadixButton>
  );
}

export default Button;