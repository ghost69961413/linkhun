import type { LoginInput, RegisterInput } from '../types/auth';

export type LoginField = 'usernameOrEmail' | 'password';
export type RegisterField = keyof RegisterInput;
export type ValidationErrors<T extends string> = Partial<Record<T, string>>;

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const usernamePattern = /^[a-zA-Z0-9][a-zA-Z0-9._-]{2,29}$/;

export function splitFullName(fullName: string): string[] {
  return fullName.trim().split(/\s+/).filter(Boolean);
}

export function validateLogin(values: Pick<LoginInput, 'usernameOrEmail' | 'password'>): ValidationErrors<LoginField> {
  const errors: ValidationErrors<LoginField> = {};
  const usernameOrEmail = values.usernameOrEmail.trim();
  if (!usernameOrEmail) errors.usernameOrEmail = 'Enter your username or email address.';
  else if (usernameOrEmail.includes('@') ? !emailPattern.test(usernameOrEmail) : !usernamePattern.test(usernameOrEmail)) errors.usernameOrEmail = usernameOrEmail.includes('@') ? 'Enter a valid email address.' : 'Enter a valid username.';
  if (!values.password) errors.password = 'Enter your password.';
  else if (values.password.length < 8) errors.password = 'Your password must be at least 8 characters.';
  return errors;
}

export function getPasswordStrength(password: string) {
  if (!password) return { score: 0, label: 'Use at least 8 characters', hint: 'Include upper and lower case letters and a number.' };
  const checks = [password.length >= 8, /[a-z]/.test(password), /[A-Z]/.test(password), /\d/.test(password), /[^a-zA-Z0-9]/.test(password)];
  const score = checks.filter(Boolean).length;
  const labels = ['Too short', 'Weak', 'Fair', 'Good', 'Strong', 'Excellent'];
  const hints = [
    'Use at least 8 characters.',
    'Add more characters and mix letter cases.',
    'Try adding an uppercase letter or a number.',
    'A symbol can make it even stronger.',
    'Nice work. Your password is strong.',
    'Excellent. Your password is very strong.',
  ];
  return { score, label: labels[score], hint: hints[score] };
}

export function validateRegister(values: RegisterInput): ValidationErrors<RegisterField> {
  const errors: ValidationErrors<RegisterField> = {};
  const nameParts = splitFullName(values.fullName);
  if (!values.fullName.trim()) errors.fullName = 'Enter your full name.';
  else if (nameParts.length < 2) errors.fullName = 'Enter your first and last name.';
  else if (values.fullName.trim().length > 100) errors.fullName = 'Your name must be 100 characters or fewer.';
  const username = values.username.trim();
  if (!username) errors.username = 'Choose a username.';
  else if (!usernamePattern.test(username)) errors.username = 'Use 3–30 letters, numbers, dots, underscores, or hyphens. Start with a letter or number.';
  const email = values.email.trim();
  if (!email) errors.email = 'Enter your email address.';
  else if (!emailPattern.test(email)) errors.email = 'Enter a valid email address.';
  if (!values.password) errors.password = 'Create a password.';
  else if (values.password.length < 8) errors.password = 'Use at least 8 characters.';
  else if (!/[a-z]/.test(values.password) || !/[A-Z]/.test(values.password) || !/\d/.test(values.password)) errors.password = 'Use an uppercase letter, a lowercase letter, and a number.';
  if (!values.confirmPassword) errors.confirmPassword = 'Confirm your password.';
  else if (values.password !== values.confirmPassword) errors.confirmPassword = 'Passwords do not match.';
  return errors;
}
