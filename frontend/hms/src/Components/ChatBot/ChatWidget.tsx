import { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import {
  ActionIcon,
  Box,
  Group,
  Loader,
  Paper,
  ScrollArea,
  Stack,
  Text,
  TextInput,
  Tooltip,
} from "@mantine/core";
import {
  IconMessage2,
  IconSend,
  IconX,
  IconRefresh,
} from "@tabler/icons-react";
import { sendChatMessage, clearChatHistory } from "../../Service/ChatService";

interface Message {
  role: "user" | "bot";
  text: string;
}

const CONV_KEY = "hms_chat_id";

function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
}

function getOrCreateConversationId(): string {
  let id = localStorage.getItem(CONV_KEY);
  if (!id) {
    id = generateId();
    localStorage.setItem(CONV_KEY, id);
  }
  return id;
}

export default function ChatWidget() {
  const token = useSelector((state: any) => state.jwt);
  const user = useSelector((state: any) => state.user);
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId, setConversationId] = useState<string>(() =>
    getOrCreateConversationId()
  );
  const viewport = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (viewport.current) {
      viewport.current.scrollTo({ top: viewport.current.scrollHeight, behavior: "smooth" });
    }
  }, [messages, isLoading]);

  if (!token) return null;

  const handleSend = async () => {
    const text = input.trim();
    if (!text || isLoading) return;
    setInput("");
    setMessages((prev) => [...prev, { role: "user", text }]);
    setIsLoading(true);
    try {
      const data = await sendChatMessage({
        message: text,
        conversationId,
        role: user?.role,
        userName: user?.name,
        profileId: user?.profileId,
      });
      setMessages((prev) => [...prev, { role: "bot", text: data.response }]);
    } catch {
      setMessages((prev) => [
        ...prev,
        { role: "bot", text: "Sorry, I couldn't reach the server. Please try again." },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNewChat = async () => {
    await clearChatHistory(conversationId).catch(() => {});
    const newId = generateId();
    localStorage.setItem(CONV_KEY, newId);
    setConversationId(newId);
    setMessages([]);
  };

  return (
    <>
      {/* Floating toggle button */}
      <Box
        style={{
          position: "fixed",
          bottom: 28,
          right: 28,
          zIndex: 1000,
        }}
      >
        {!isOpen && (
          <Tooltip label="HMS Assistant" position="left" withArrow>
            <ActionIcon
              size={56}
              radius="xl"
              variant="gradient"
              gradient={{ from: "primary.4", to: "primary.8", deg: 132 }}
              onClick={() => setIsOpen(true)}
              style={{ boxShadow: "0 4px 16px rgba(0,0,0,0.25)" }}
            >
              <IconMessage2 size={26} />
            </ActionIcon>
          </Tooltip>
        )}
      </Box>

      {/* Chat window */}
      {isOpen && (
        <Paper
          shadow="xl"
          radius="lg"
          style={{
            position: "fixed",
            bottom: 28,
            right: 28,
            width: 380,
            height: 520,
            zIndex: 1000,
            display: "flex",
            flexDirection: "column",
            overflow: "hidden",
            border: "1px solid var(--mantine-color-primary-2)",
          }}
        >
          {/* Header */}
          <Box
            style={{
              background: "linear-gradient(132deg, var(--mantine-color-primary-4), var(--mantine-color-primary-8))",
              padding: "12px 16px",
            }}
          >
            <Group justify="space-between" align="center">
              <Group gap={8}>
                <IconMessage2 size={20} color="white" />
                <Text fw={600} size="sm" c="white">
                  HMS Assistant
                </Text>
              </Group>
              <Group gap={4}>
                <Tooltip label="New chat" withArrow>
                  <ActionIcon
                    variant="subtle"
                    color="white"
                    size="sm"
                    onClick={handleNewChat}
                  >
                    <IconRefresh size={16} />
                  </ActionIcon>
                </Tooltip>
                <ActionIcon
                  variant="subtle"
                  color="white"
                  size="sm"
                  onClick={() => setIsOpen(false)}
                >
                  <IconX size={16} />
                </ActionIcon>
              </Group>
            </Group>
          </Box>

          {/* Messages */}
          <ScrollArea flex={1} viewportRef={viewport} p="xs">
            <Stack gap={8} p={4}>
              {messages.length === 0 && (
                <Text size="xs" c="dimmed" ta="center" mt="xl">
                  Ask me anything about appointments, patients, doctors, medicines, and more!
                </Text>
              )}
              {messages.map((msg, i) => (
                <Box
                  key={i}
                  style={{
                    display: "flex",
                    justifyContent: msg.role === "user" ? "flex-end" : "flex-start",
                  }}
                >
                  <Box
                    style={{
                      maxWidth: "80%",
                      padding: "8px 12px",
                      borderRadius: msg.role === "user" ? "16px 16px 4px 16px" : "16px 16px 16px 4px",
                      background:
                        msg.role === "user"
                          ? "var(--mantine-color-primary-4)"
                          : "var(--mantine-color-neutral-1)",
                      color: msg.role === "user" ? "white" : "var(--mantine-color-neutral-9)",
                    }}
                  >
                    <Text size="sm" style={{ whiteSpace: "pre-wrap", wordBreak: "break-word" }}>
                      {msg.text}
                    </Text>
                  </Box>
                </Box>
              ))}
              {isLoading && (
                <Box style={{ display: "flex", justifyContent: "flex-start" }}>
                  <Box
                    style={{
                      padding: "10px 14px",
                      borderRadius: "16px 16px 16px 4px",
                      background: "var(--mantine-color-neutral-1)",
                    }}
                  >
                    <Loader size="xs" color="primary" type="dots" />
                  </Box>
                </Box>
              )}
            </Stack>
          </ScrollArea>

          {/* Input */}
          <Box p="xs" style={{ borderTop: "1px solid var(--mantine-color-neutral-2)" }}>
            <Group gap={6}>
              <TextInput
                flex={1}
                placeholder="Type your message…"
                value={input}
                onChange={(e) => setInput(e.currentTarget.value)}
                onKeyDown={(e) => e.key === "Enter" && !e.shiftKey && handleSend()}
                disabled={isLoading}
                radius="xl"
                size="sm"
              />
              <ActionIcon
                size={36}
                radius="xl"
                variant="filled"
                color="primary"
                onClick={handleSend}
                disabled={isLoading || !input.trim()}
              >
                <IconSend size={16} />
              </ActionIcon>
            </Group>
          </Box>
        </Paper>
      )}
    </>
  );
}
