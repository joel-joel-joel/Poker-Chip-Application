import { Button as RadixButton } from "@radix-ui/themes";
import type { ReactNode, CSSProperties } from "react";
import "./button.css";

type ButtonProps = {
  children: ReactNode;
  onClick?: () => void;
  variant?: "solid" | "soft" | "outline" | "ghost";
  color?: "gray" | "blue" | "green" | "red";   
  size?: "1" | "2" | "3";
  className?: string;
  style?: CSSProperties;
};

export default function Button({
  children,
  onClick,
  variant = "solid",
  color,                  
  size = "3",
  className,
  style,
}: ButtonProps) {
  return (
    <RadixButton
      onClick={onClick}
      variant={variant}
      color={color}     
      size={size}
      className={className}
      style={style}
    >
      {children}
    </RadixButton>
  );
}