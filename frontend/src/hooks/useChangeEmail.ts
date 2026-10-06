import { useMutation } from '@tanstack/react-query';
import { userService, type ChangeEmailInput } from '../services/userService';

export function useChangeEmail() {
  return useMutation({ mutationFn: (input: ChangeEmailInput) => userService.changeEmail(input) });
}
