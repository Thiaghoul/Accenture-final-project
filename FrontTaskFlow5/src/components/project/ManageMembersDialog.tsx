import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { toast } from "sonner"
import { projectService, MemberRoles, UserResponse } from "@/services/project.service"
import { userService } from "@/services/user.service"

interface ManageMembersDialogProps {
  projectId: string
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ManageMembersDialog({ projectId, open, onOpenChange }: ManageMembersDialogProps) {
  const [members, setMembers] = useState<UserResponse[]>([])
  const [allUsers, setAllUsers] = useState<UserResponse[]>([])
  const [selectedUser, setSelectedUser] = useState<string | null>(null)
  const [selectedRole, setSelectedRole] = useState<MemberRoles>(MemberRoles.VIEWER)

  useEffect(() => {
    if (open) {
      fetchMembers()
      fetchAllUsers()
    }
  }, [open, projectId])

  const fetchMembers = async () => {
    try {
      const projectMembers = await projectService.getMembers(projectId)
      setMembers(projectMembers)
    } catch (error) {
      console.error("Failed to fetch project members:", error)
      toast.error("Falha ao carregar membros do projeto.")
    }
  }

  const fetchAllUsers = async () => {
    try {
      const users = await userService.getAllUsers()
      setAllUsers(users)
    } catch (error) {
      console.error("Failed to fetch all users:", error)
      toast.error("Falha ao carregar usuários.")
    }
  }

  const handleAddMember = async () => {
    if (!selectedUser || !selectedRole) return

    try {
      await projectService.addMember(projectId, selectedUser, selectedRole)
      toast.success("Membro adicionado com sucesso!")
      setSelectedUser(null)
      setSelectedRole(MemberRoles.VIEWER)
      fetchMembers()
    } catch (error: any) {
      console.error("Failed to add member:", error)
      toast.error(error.response?.data?.message || "Falha ao adicionar membro.")
    }
  }

  const handleRemoveMember = async (userId: string) => {
    try {
      await projectService.removeMember(projectId, userId)
      toast.success("Membro removido com sucesso!")
      fetchMembers()
    } catch (error: any) {
      console.error("Failed to remove member:", error)
      toast.error(error.response?.data?.message || "Falha ao remover membro.")
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Gerenciar Membros do Projeto</DialogTitle>
        </DialogHeader>
        <div className="space-y-4">
          <div>
            <Label htmlFor="add-member-user">Adicionar Membro</Label>
            <div className="flex gap-2 mt-2">
              <Select onValueChange={setSelectedUser} value={selectedUser || ""}>
                <SelectTrigger id="add-member-user">
                  <SelectValue placeholder="Selecionar usuário" />
                </SelectTrigger>
                <SelectContent>
                  {allUsers.map((user) => (
                    <SelectItem key={user.id} value={user.id}>
                      {user.firstName} {user.lastName} ({user.email})
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Select onValueChange={(value) => setSelectedRole(value as MemberRoles)} value={selectedRole}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Selecionar função" />
                </SelectTrigger>
                <SelectContent>
                  {Object.values(MemberRoles).map((role) => (
                    <SelectItem key={role} value={role}>
                      {role}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Button onClick={handleAddMember} disabled={!selectedUser}>
                Adicionar
              </Button>
            </div>
          </div>

          <div>
            <Label className="block mb-2">Membros Atuais</Label>
            {members.length === 0 ? (
              <p className="text-sm text-neutral-500">Nenhum membro no projeto ainda.</p>
            ) : (
              <ul className="space-y-2">
                {members.map((member) => (
                  <li key={member.id} className="flex items-center justify-between bg-neutral-50 p-3 rounded-md">
                    <div>
                      <p className="font-medium">{member.firstName} {member.lastName}</p>
                      <p className="text-sm text-neutral-500">{member.email}</p>
                    </div>
                    <Button variant="destructive" size="sm" onClick={() => handleRemoveMember(member.id)}>
                      Remover
                    </Button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
